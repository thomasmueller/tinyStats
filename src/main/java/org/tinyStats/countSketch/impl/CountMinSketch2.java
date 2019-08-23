package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.util.Hash;

public class CountMinSketch2 implements CountSketch {
    final static long K = 3;
    final static int BITS = 2;
    final static int MAX = 3;
    final static int B2 = 5;
    final static int M2 = 31;
    long count;
    int[] data = new int[32];

    @Override
    public void add(long hash) {
        int zeros = Long.numberOfTrailingZeros(count++);
        int min = 0x8;
        int minShift = -1;
        for (int k = 0; k < K; k++) {
            int i = (int) ((hash >>> (k * B2)) & M2);
            int m = data[i];
            if (m < min) {
                min = m;
                minShift = i;
            }
        }
        if (zeros < min) {
            return;
        }
        if (min == MAX) {
            for (int i = 0; i < data.length; i++) {
                boolean dec = true;
                for (int k = 0; k < K; k++) {
                    int ii = (int) ((hash >>> (k * B2)) & M2);
                    if (ii == i) {
                        dec = false;
                        break;
                    }
                }
                if (dec) {
                    int zeros2 = Long.numberOfTrailingZeros(Hash.hash64(count++, 1));
                    if (zeros2 > data[i]) {
                        data[i] = Math.max(0, data[i] - 1);
                    }
                }
            }
            min--;
        }
        data[minShift] = min + 1;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    @Override
    public long estimate(long hash) {
        int min = 0xf;
        for (int k = 0; k < K; k++) {
            int i = (int) ((hash >>> (k * B2)) & M2);
            int m = data[i];
            min = Math.min(min, m);
        }
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            int m = data[i];
            sum += m;
        }
        double avg = sum / (double) data.length;
        return (int) (100 * Math.pow(2, min - avg));
    }

    @Override
    public long estimateRepeatRate() {
        return 0;
    }

}