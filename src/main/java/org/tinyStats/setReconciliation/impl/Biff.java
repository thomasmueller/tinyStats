package org.tinyStats.setReconciliation.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;

import org.tinyStats.util.Hash;

/**
 * Biff (Bloom Filter) Codes: Fast Error Correction for Large Data Sets - https://arxiv.org/abs/1208.0798
 * Invertible Bloom Lookup Table - https://arxiv.org/abs/1101.2245
 */
public class Biff {

    private static final int K = 3;

    private long[] keys;
    private byte[] checksums;
    private int blockLength;

    public Biff(int sizeInBytes) {
        int size = sizeInBytes / 9;
        blockLength = (size + K - 1) / K;
        size = blockLength * K;
        keys = new long[size];
        checksums = new byte[size];
    }

    public Biff(byte[] data) {
        this(data.length);
        ByteBuffer buff = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < keys.length; i++) {
            keys[i] = buff.getLong();
        }
        for (int i = 0; i < checksums.length; i++) {
            checksums[i] = buff.get();
        }
    }

    public byte[] getBytes() {
        byte[] data = new byte[(int) getSizeInBytes()];
        ByteBuffer buff = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < keys.length; i++) {
            buff.putLong(keys[i]);
        }
        for (int i = 0; i < checksums.length; i++) {
            buff.put(checksums[i]);
        }
        return data;
    }

    public long getSizeInBytes() {
        return 8 * keys.length + checksums.length;
    }

    public HashSet<Long> list() {
        HashSet<Long> seen = new HashSet<>();
        while (true) {
            boolean found = false;
            for (int i = 0; i < keys.length; i++) {
                if (isAlone(i)) {
                    long k = keys[i];
                    if (!seen.add(k)) {
                        return null;
                    }
                    xor(k);
                    found = true;
                }
            }
            if (!found) {
                return seen;
            }
        }
    }

    private boolean isAlone(int index) {
        long key = keys[index];
        if (getChecksum(key) != checksums[index]) {
            return false;
        }
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

    private static byte getChecksum(long key) {
        return (byte) Hash.hash64(key, 10);
    }

    public void xor(long key) {
        int checksum = getChecksum(key);
        long a = Hash.hash64(key, 1);
        long b = Hash.hash64(key, 2);
        for (int i = 0; i < K; i++) {
            int m = i * blockLength + Hash.reduce((int) a, blockLength);
            keys[m] ^= key;
            checksums[m] ^= checksum;
            a += b;
        }
    }

}
