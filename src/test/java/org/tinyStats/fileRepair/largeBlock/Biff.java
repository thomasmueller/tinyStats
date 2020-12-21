package org.tinyStats.fileRepair.largeBlock;

import java.util.List;

import org.tinyStats.util.Hash;
import org.tinyStats.util.Murmur2;

/**
 * Biff (Bloom Filter) Codes: Fast Error Correction for Large Data Sets
 * Invertible Bloom Lookup Table.
 *
 * https://arxiv.org/abs/1101.2245
 */
public class Biff {

    public static int KEY_LENGTH = 16 + 4;

    private Cell[] cells;

    static class Cell {
        int count;
        byte[] key = new byte[KEY_LENGTH];
        long value;

        static int getSizeInBytes() {
            return KEY_LENGTH * 8 + 8 + 1;
        }
    }

    private long size;
    private int blockLength;

    private static final int K = 3;

    public Biff(int size) {
        blockLength = (size + K - 1) / K;
        size = blockLength * K;
        cells = new Cell[size];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell();
        }
    }

    public long getSizeInBytes() {
        return cells.length * Cell.getSizeInBytes();
    }

    public void add(byte[] key) {
        addOrRemove(key, 1);
    }

    public void remove(byte[] key) {
        addOrRemove(key, -1);
    }

    public boolean list(List<byte[]> added) {
        if (Math.abs(size) > cells.length) {
            return false;
        }
        while (true) {
            boolean found = false;
            for (int i = 0; i < cells.length; i++) {
                Cell cell = cells[i];
                int c = cell.count;
                if (c == 1) {
                    byte[] k = cell.key;
                    long v = cell.value;
                    if (isKey(k, v, i)) {
                        added.add(k.clone());
                        addOrRemove(k, -1);
                        found = true;
                    }
                } else if (c == -1) {
                    byte[] k = cell.key;
                    long v = cell.value;
                    if (isKey(k, v, i)) {
                        addOrRemove(k, 1);
                        found = true;
                    }
                }
            }
            if (!found) {
                break;
            }
        }
        return size == 0;
    }

    boolean isKey(byte[] key, long value, int index) {
        long k = Murmur2.hash64(key, key.length, 10);
        return k == value;
//        if (k != value) {
//            return false;
//        }
//        long a = Hash.hash64(k, 1);
//        long b = Hash.hash64(k, 2);
//        for (int i = 0; i < K; i++) {
//            int m = i * blockLength + Hash.reduce((int) a, blockLength);
//            if (m == index) {
//                return true;
//            }
//            a += b;
//        }
//        return false;
    }

    public void addOrRemove(byte[] key, int inc) {
        long k = Murmur2.hash64(key, key.length, 10);
        long value = k;
        size += inc;
        long a = Hash.hash64(k, 1);
        long b = Hash.hash64(k, 2);
        for (int i = 0; i < K; i++) {
            int m = i * blockLength + Hash.reduce((int) a, blockLength);
            Cell cell = cells[m];
            for (int j = 0; j < key.length; j++) {
                cell.key[j] ^= key[j];
            }
            cell.value ^= value;
            cell.count += inc;
            a += b;
        }
    }

}
