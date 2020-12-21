package org.tinyStats.setReconciliation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.tinyStats.setReconciliation.impl.Biff;

public class FileRepairTest {

    private Random r = new Random(1);

    public static void main(String... args) {
        new FileRepairTest().test(1024 * 1024);
    }

    @Test
    public void test() {
        test(100);
    }

    void test(int dataLength) {
        byte[] data = createData(dataLength);
        for (int errorCount = 10; errorCount < 10 * data.length; errorCount *= 1.5) {
            for (int mapSize = 10;; mapSize *= 1.5) {
                int correct = 0;
                long mapSizeInBytes = 0;
                Biff map = createMap(data, mapSize);
                byte[] mapBytes = map.getBytes();
                mapSizeInBytes = mapBytes.length;
                for (int i = 0; i < 10; i++) {
                    correct += corruptAndTryRepairing(data, errorCount, mapBytes) ? 1 : 0;
                }
                if (correct == 10) {
                    System.out.println("errors: " + errorCount + " map: " + mapSize + " corrected: " + correct * 10 + "% " + mapSizeInBytes + " bytes");
                    break;
                }
            }
        }
    }

    byte[] createData(int dataLength) {
        byte[] data = new byte[dataLength];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) r.nextInt();
        }
        return data;
    }

    boolean corruptAndTryRepairing(byte[] data, int errorCount, byte[] mapBytes) {
        Biff map = new Biff(mapBytes);
        byte[] corrupt = data.clone();
        for (int i = 0; i < errorCount; i++) {
            int p = r.nextInt(corrupt.length);
            corrupt[p] = (byte) r.nextInt();
        }
        repair(corrupt, map);
        for (int i = 0; i < data.length; i++) {
            if (data[i] != corrupt[i]) {
                return false;
            }
        }
        return true;
    }

    static Biff createMap(byte[] data, int mapSize) {
        Biff map = new Biff(mapSize);
        for (int i = 0; i < data.length; i += 4) {
            long k = ((long) (i / 4) << 32) | (readInt(data, i) & 0xffffffffL);
            map.xor(k);
        }
        return map;
    }


    static boolean repair(byte[] data, Biff repair) {
        for (int i = 0; i < data.length; i += 4) {
            long k = ((long) (i / 4) << 32) | (readInt(data, i) & 0xffffffffL);
            repair.xor(k);
        }
        Set<Long> found = repair.list();
        if (found == null) {
            return false;
        }
        ArrayList<Long> correct = new ArrayList<>();
        for(long x : found) {
            int p = (int) (x >>> 32) * 4;
            if (p > data.length || p < 0) {
                return false;
            }
            int value = (int) (x & 0xffffffffL);
            if (readInt(data, p) != value) {
                correct.add(x);
            }
        }
        for(long x : correct) {
            int p = (int) (x >>> 32) * 4;
            int value = (int) (x & 0xffffffffL);
            writeInt(data, p, value);
        }
        return true;
    }

    public static int readInt(byte[] buff, int pos) {
        return (buff[pos++] << 24) + ((buff[pos++] & 0xff) << 16) + ((buff[pos++] & 0xff) << 8) + (buff[pos] & 0xff);
    }

    public static void writeInt(byte[] buff, int pos, int x) {
        buff[pos++] = (byte) (x >> 24);
        buff[pos++] = (byte) (x >> 16);
        buff[pos++] = (byte) (x >> 8);
        buff[pos] = (byte) x;
    }
}
