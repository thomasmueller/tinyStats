package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.util.Hash;

/**
 * Alon-Matias-Szegedy (AMS) sketches, from "The space complexity of
 * approximating the frequency moments".
 *
 * This implementation returns percentages instead of counts.
 *
 * See also "Using the AMS Sketch to estimate inner products" (can be used to
 * estimate join sizes between tables, see
 * https://github.com/twitter/algebird/issues/563
 *
 * https://github.com/mayconbordin/streaminer/blob/master/src/main/java/org/streaminer/stream/frequency/AMSSketch.java
 */
public class AMSSketch implements CountSketch {

    private final int depth, buckets;
    private long totalCount;
    private final int[][] counts;

    public AMSSketch(int depth, int buckets) {
        if (Integer.bitCount(depth) != 1) {
            throw new IllegalArgumentException("Depth must be a power of 2");
        }
        this.depth = depth;
        this.buckets = buckets;
        counts = new int[buckets][depth];
    }

    @Override
    public void add(long hash) {
        totalCount++;
        for (int i = 0; i < buckets; i++) {
            long d = Hash.hash64(hash, i);
            int x = (d & 1) == 0 ? -1 : 1;
            counts[i][(int) (d >>> 1) & (depth - 1)] += x;
        }
    }

    @Override
    public long estimate(long hash) {
        int[] est = new int[buckets];
        for (int i = 0; i < buckets; i++) {
            long d = Hash.hash64(hash, i);
            int x = (d & 1) == 0 ? -1 : 1;
            est[i] = x * counts[i][(int) (d >>> 1) & (depth - 1)];
        }
        Arrays.sort(est);
        return est[buckets / 2] * 100 / totalCount;
    }

    @Override
    public long estimateRepeatRate() {
        long[] est = new long[buckets];
        for (int i = 0; i < buckets; i++) {
            long sum = 0;
            for (int j = 0; j < depth; j++) {
                long c = counts[i][j];
                sum += c * c;
            }
            est[i] = sum;
        }
        Arrays.sort(est);
        return (int) Math.sqrt(est[buckets / 2]) * 100 / totalCount;
    }

}
