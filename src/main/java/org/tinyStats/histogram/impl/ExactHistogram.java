package org.tinyStats.histogram.impl;

import org.tinyStats.histogram.Histogram;

/**
 * An exact histogram. It uses a 64-bit counter for each bucket.
 */
public class ExactHistogram implements Histogram {

    private long[] counts;

    public ExactHistogram(int bucketCount) {
        counts = new long[12];
    }

    @Override
    public void add(long hash, int bucket) {
        counts[bucket]++;
    }

    @Override
    public int[] getHistogram() {
        long sum = 0;
        for (long x : counts) {
            sum += x;
        }
        int[] result = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            long c = counts[i];
            if (c > 0) {
                result[i] = Math.max(1, (int) (100 * c / sum));
            }
        }
        return result;
    }

}
