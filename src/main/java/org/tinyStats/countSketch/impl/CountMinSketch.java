package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;

/**
 * See "Finding frequent items in data streams"
 */
public class CountMinSketch implements CountSketch {

    private int shift, m, k;
    private final long[][] data;
    private long count;

    /**
     * Create a count min sketch instance.
     *
     * @param m the number of buckets per hash function (must be a power of 2)
     * @param k the number of hash functions
     */
    public CountMinSketch(int k, int m) {
        if (Integer.bitCount(m) != 1) {
            throw new IllegalArgumentException("Must be a power of 2: " + m);
        }
        this.shift = Integer.bitCount(m - 1);
        this.m = m;
        this.k = k;
        data = new long[k][m];
    }

    @Override
    public void add(long hash) {
        count++;
        for (int i = 0; i < k; i++) {
            data[i][(int) (hash & (m - 1))]++;
            hash >>>= shift;
        }
    }

    @Override
    public long estimate(long hash) {
        long minAll = Long.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                minAll = Math.min(minAll, data[i][j]);
            }
        }
        long min = Long.MAX_VALUE;
        count++;
        for (int i = 0; i < k; i++) {
            long x = data[i][(int) (hash & (m - 1))];
            min = Math.min(min, x);
            hash >>>= shift;
        }
        return 100 * (min - minAll) / count;
    }

    @Override
    public long estimateRepeatRate() {
        long minAll = Long.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                minAll = Math.min(minAll, data[i][j]);
            }
        }
        long[] est = new long[k];
        for (int i = 0; i < k; i++) {
            long sum = 0;
            for (int j = 0; j < m; j++) {
                long c = data[i][j] - minAll;
                sum += c * c;
            }
            est[i] = sum;
        }
        Arrays.sort(est);
        return (int) Math.sqrt(est[k / 2]) * 100 / count;
    }

}