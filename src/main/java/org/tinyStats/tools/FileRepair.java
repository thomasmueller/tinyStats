package org.tinyStats.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.tinyStats.setReconciliation.impl.Biff;

public class FileRepair {

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("A tool to repair a corrupt file using biff file, and create biff files (similar to PAR2)");
            System.out.println("Usage:");
            System.out.println("c <biff file> <data file> - create a biff file from a data file");
            System.out.println("r <biff file> <data file> - try to repair a data file using a biff file");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if ("c".equals(args[i])) {
                String biffFileName = args[++i];
                String inputFileName = args[++i];
                createBiff(biffFileName, inputFileName);
            } else if ("r".equals(args[i])) {
                String biffFileName = args[++i];
                String inputFileName = args[++i];
                repair(biffFileName, inputFileName);
            }
        }
    }

    private static void apply(Biff biff, RandomAccessFile in) throws IOException {
        long len = in.length();
        int blockSize = 8 * 1024;
        byte[] block = new byte[blockSize];
        long lastLog = System.currentTimeMillis();
        for (long i = 0; i < len; i += blockSize) {
            if ((i & 0xffffL) == 0) {
                long now = System.currentTimeMillis();
                if (now > lastLog + 1000) {
                    System.out.println((100 * i / len) + "%");
                    lastLog = now;
                }
            }
            Arrays.fill(block, (byte) 0);
            int remaining = (int) Math.min(blockSize, len - i);
            in.readFully(block, 0, remaining);
            for (int j = 0; j < remaining; j += 4) {
                long k = ((long) ((i + j) / 4) << 32L) | (readInt(block, j) & 0xffffffffL);
                biff.xor(k);
            }
        }
    }

    private static void repair(String biffFileName, String inputFileName) throws IOException {
        Biff biff;
        try (RandomAccessFile bIn = new RandomAccessFile(biffFileName, "r")) {
            byte[] data = new byte[(int) bIn.length()];
            bIn.readFully(data);
            biff = new Biff(data);
        }
        try (RandomAccessFile in = new RandomAccessFile(inputFileName, "r")) {
            apply(biff, in);
        }
        Set<Long> found = biff.list();
        if (found == null) {
            System.out.println("Can't correct");
            return;
        }
        ArrayList<Long> correct = new ArrayList<>();
        try (RandomAccessFile out = new RandomAccessFile(inputFileName, "rw")) {
            long size = out.length();
            correct.sort(Long::compare);
            byte[] buff = new byte[4];
            for (long x : found) {
                int p = (int) (x >>> 32) * 4;
                if (p > size || p < 0) {
                    return;
                }
                int value = (int) (x & 0xffffffffL);
                Arrays.fill(buff, (byte) 0);
                out.seek(p);
                int remaining = (int) Math.min(4, size - p);
                out.readFully(buff, 0, remaining);
                if (readInt(buff, 0) != value) {
                    correct.add(x);
                }
            }
            for (long x : correct) {
                long p = (x >>> 32) * 4L;
                int value = (int) (x & 0xffffffffL);
                writeInt(buff, 0, value);
                out.seek(p);
                int remaining = (int) Math.min(4, size - p);
                out.write(buff, 0, remaining);
            }
        }
    }

    private static void createBiff(String biffFileName, String inputFileName) throws IOException {
        Biff biff;
        try (RandomAccessFile in = new RandomAccessFile(inputFileName, "r")) {
            long len = in.length();
            long maxSize = 4 * (1L << 32);
            if (len > maxSize) {
                throw new IllegalArgumentException("File too large: " + inputFileName + " maximum size " + maxSize + " bytes");
            }
            long repairSize = (long) (len * 0.05) + 128;
            if (repairSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Repair file size too large: " + repairSize);
            }
            biff = new Biff((int) repairSize);
            apply(biff, in);
        }
        new File(biffFileName).delete();
        try (RandomAccessFile out = new RandomAccessFile(biffFileName, "rw")) {
            out.write(biff.getBytes());
        }
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
