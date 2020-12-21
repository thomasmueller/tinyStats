package org.tinyStats.fileRepair.largeBlock;

import java.util.ArrayList;
import java.util.Random;

import org.tinyStats.util.Murmur2;

public class FileRepair {

    Random r = new Random(1);

    public static void main(String... arg) {
        new FileRepair().test(1 * 1024 * 1024);
    }

    void test(int dataLength) {
        byte[] data = createData(dataLength);
        for (int errorCount = 10; errorCount < 10 * data.length; errorCount *= 1.5) {
            for (int mapSize = 10;; mapSize *= 1.5) {
                int correct = 0;
                long mapSizeInBytes = 0;
                for (int i = 0; i < 10; i++) {
                    Biff map = createMap(data, mapSize);
                    mapSizeInBytes = map.getSizeInBytes();
                    correct += testRepair(data, errorCount, map) ? 1 : 0;
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
            data[i] = (byte) (r.nextInt() & 0xff);
        }
        return data;
    }

    boolean testRepair(byte[] data, int errorCount, Biff map) {
        byte[] corrupt = data.clone();
        for (int i = 0; i < errorCount; i++) {
            int p = r.nextInt(corrupt.length);
//System.out.println("corrupt[" + p + "]=" + corrupt[p]);
            corrupt[p] = (byte) (r.nextInt() & 0xff);
//            System.out.println("corrupt[" + p + "] =>" + corrupt[p]);
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
        for (int i = 0; i < data.length; i += Biff.KEY_LENGTH - 4) {
            byte[] k = new byte[Biff.KEY_LENGTH];
            writeInt(k, 0, i);
            System.arraycopy(data, i, k, 4, Biff.KEY_LENGTH - 4);
//long x = Murmur2.hash64(k, k.length, 0);
//System.out.println(" create " + i + " h=" + x);

            map.add(k);
        }
        return map;
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

    static boolean repair(byte[] data, Biff repair) {
        for (int i = 0; i < data.length; i += Biff.KEY_LENGTH - 4) {
            byte[] k = new byte[Biff.KEY_LENGTH];
            writeInt(k, 0, i);
            System.arraycopy(data, i, k, 4, Biff.KEY_LENGTH - 4);
//long x = Murmur2.hash64(k, k.length, 0);
//System.out.println(" repair " + i + " h=" + x);
            repair.remove(k);
        }
        ArrayList<byte[]> added = new ArrayList<>();
        repair.list(added);
        for(byte[] x : added) {
            int p = readInt(x, 0);
            if (p > data.length || p < 0) {
                return false;
            }
            System.arraycopy(x, 4, data, p, Biff.KEY_LENGTH - 4);
        }
        return true;
    }
}
