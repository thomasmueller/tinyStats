package org.tinyStats.countDistinctSketch.impl;

import java.util.Arrays;

import org.tinyStats.cardinality.impl.int64.HyperLogLog2Linear64;
import org.tinyStats.countDistinctSketch.CountDistinctSketch;
import org.tinyStats.util.Hash;

/**
 * The combination of HyperLogLog and count-min-mean sketch.
 *
 * See "Sketch Techniques for Approximate Query Processing" section 1.5.2.2 "Combinations of Sketches".
 */
public class HyperLogLogMinMeanSketch implements CountDistinctSketch {

    private int shift, m, k;
    private final long[][] data;
    private long countDistinct;

    /**
     * Create a count min sketch instance.
     *
     * @param m the number of buckets per hash function (must be a power of 2)
     * @param k the number of hash functions
     */
    public HyperLogLogMinMeanSketch(int k, int m) {
        if (Integer.bitCount(m) != 1) {
            throw new IllegalArgumentException("Must be a power of 2: " + m);
        }
        if ((k & 1) == 0) {
            throw new IllegalArgumentException("Must be odd: " + k);
        }
        this.shift = Integer.bitCount(m - 1);
        if (shift * k > 64) {
            throw new IllegalArgumentException("Too many hash functions or buckets: " + k + " / " + m);
        }
        this.m = m;
        this.k = k;
        data = new long[k][m];
    }

    protected long countDistinct(long data, long hash) {
        return HyperLogLog2Linear64.add(data, hash);
    }

    protected long estimateDistinct(long data) {
        return HyperLogLog2Linear64.estimate(data);
    }

    @Override
    public void add(long keyHash, long valueHash) {
        countDistinct = countDistinct(countDistinct, Hash.hash64(keyHash, valueHash));
        for (int i = 0; i < k; i++) {
            int index = (int) (keyHash & (m - 1));
            long old = data[i][index];
            data[i][index] = countDistinct(old, valueHash);
            keyHash >>>= shift;
        }
    }

    @Override
    public long estimate(long keyHash) {
        long[] array = new long[k];
        long min = Long.MAX_VALUE;
        long distinct = estimateDistinct(countDistinct);
        for (int i = 0; i < k; i++) {
            long x = estimateDistinct(data[i][(int) (keyHash & (m - 1))]);
            min = Math.min(min, x);
            array[i] = Math.max(0, x - (distinct - x) / (m - 1));
            keyHash >>>= shift;
        }
        Arrays.sort(array);
        long mean = array[array.length / 2];
        return Math.min(mean, min);
    }

}
