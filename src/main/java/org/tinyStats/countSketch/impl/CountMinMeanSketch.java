package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;

/**
 * The count-min-mean sketch. It has lower bias than the count-min sketch if
 * there are many entries and the number of buckets and hash functions is small.
 * The minimum of the mean and count-min is returned.
 *
 * See "New Estimation Algorithms for Streaming Data: Count-min Can Do More" and
 * "Sketch algorithms for estimating point queries in NLP".
 *
 * Conservative updates and approximate counter are not used here, see
 * "Count-Min-Log sketch: Approximately counting with approximate counters".
 */
public class CountMinMeanSketch implements CountSketch {

    private int shift, m, k;
    private final long[][] data;
    private long count;

    /**
     * Create a count min sketch instance.
     *
     * @param m the number of buckets per hash function (must be a power of 2)
     * @param k the number of hash functions
     */
    public CountMinMeanSketch(int k, int m) {
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

    @Override
    public void add(long hash) {
        for (int i = 0; i < k; i++) {
            data[i][(int) (hash & (m - 1))]++;
            hash >>>= shift;
        }
        count++;
    }

    @Override
    public boolean estimatePercent() {
        return false;
    }

    @Override
    public long estimate(long hash) {
        long[] array = new long[k];
        long min = Long.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            long x = data[i][(int) (hash & (m - 1))];
            min = Math.min(min, x);
            array[i] = Math.max(0, x - (count - x) / (m - 1));
            hash >>>= shift;
        }
        Arrays.sort(array);
        long mean = array[array.length / 2];
        return Math.min(mean, min);
    }

    @Override
    public long estimateRepeatRate() {
        return 0;
    }

}