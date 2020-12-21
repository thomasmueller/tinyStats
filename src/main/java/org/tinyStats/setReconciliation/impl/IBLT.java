package org.tinyStats.setReconciliation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.tinyStats.util.Hash;
import org.tinyStats.util.Murmur2;

/**
 * Invertible Bloom Lookup Table.
 *
 * https://arxiv.org/abs/1101.2245
 */
public class IBLT {

    private long[] keys;
    private long[] values;
    private byte[] counts;
    private long size;
    private int blockLength;

    private static final int K = 4;

    public IBLT(int size) {
        blockLength = (size + K - 1) / K;
        size = blockLength * K;
        keys = new long[size];
        values = new long[size];
        counts = new byte[size];
    }

    public long getSizeInBytes() {
        return keys.length * 8 + values.length * 8 + counts.length;
    }

    public void add(String x) {
        addOrRemove(x, 1);
    }

    public void remove(String x) {
        addOrRemove(x, -1);
    }

    public void addOrRemove(String x, int inc) {
        byte[] bytes = x.getBytes(Murmur2.UTF8);
        long k = Murmur2.hash64(bytes, bytes.length, 0);
        for (int i = 0; i < bytes.length; i += 8) {
            long v = 0;
            for (int j = 0; j < 8 && i + j < bytes.length; j++) {
                v |= (bytes[i + j] & 0xffL) << (j * 8);
            }
            k = (k & ~1L) | ((i == 0) ? 1 : 0);
            addOrRemove(k, v, inc);
            k = Hash.hash64(k);
        }
    }

    public ArrayList<String> listAddedStrings() {
        HashMap<Long, Long> added = new HashMap<>();
        HashMap<Long, Long> removed = new HashMap<>();
        if (!list(added, removed)) {
            return null;
        }
        if (!removed.isEmpty()) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        for (long k : added.keySet()) {
            if ((k & 1) == 1) {
                ArrayList<Byte> bytes = new ArrayList<>();
                outer:
                while (true) {
                    if (!added.containsKey(k)) {
                        break;
                    }
                    long v = added.get(k);
                    for (int j = 0; j < 8; j++) {
                        byte b = (byte) ((v >>> (j * 8)) & 0xff);
                        if (b == 0) {
                            break outer;
                        }
                        bytes.add(b);
                    }
                    k = Hash.hash64(k);
                    k &= ~1L;
                }
                byte[] data = new byte[bytes.size()];
                for (int i=0; i<bytes.size(); i++) {
                    data[i] = bytes.get(i);
                }
                result.add(new String(data, Murmur2.UTF8));
            }
        }
        return result;
    }

    public void add(long key, long value) {
        addOrRemove(key, value, 1);
    }

    public void remove(long key, long value) {
        addOrRemove(key, value, -1);
    }

    public boolean list(Map<Long, Long> added, Map<Long, Long> removed) {
        if (Math.abs(size) > keys.length) {
            return false;
        }
        while (true) {
            boolean found = false;
            for (int i = 0; i < keys.length; i++) {
                int c = counts[i];
                if (c == 1) {
                    long k = keys[i];
                    if (isKey(k, i)) {
                        long v = values[i];
                        Long old = added.put(k, v);
                        if (old != null) {
                            return false;
                        }
                        addOrRemove(k, v, -1);
                        found = true;
                    }
                } else if (c == -1) {
                    long k = keys[i];
                    if (isKey(k, i)) {
                        long v = values[i];
                        Long old = removed.put(k, v);
                        if (old != null) {
                            return false;
                        }
                        addOrRemove(k, v, 1);
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

    boolean isKey(long key, int index) {
        long a = Hash.hash64(key, 1);
        long b = Hash.hash64(key, 2);
        for (int i = 0; i < K; i++) {
            int m = i * blockLength + Hash.reduce((int) a, blockLength);
            if (m == index) {
                return true;
            }
            a += b;
        }
        return false;
    }

    public void addOrRemove(long key, long value, int inc) {
        size += inc;
        long a = Hash.hash64(key, 1);
        long b = Hash.hash64(key, 2);
        for (int i = 0; i < K; i++) {
            int m = i * blockLength + Hash.reduce((int) a, blockLength);
            keys[m] ^= key;
            values[m] ^= value;
            counts[m] += inc;
            a += b;
        }
    }

}
