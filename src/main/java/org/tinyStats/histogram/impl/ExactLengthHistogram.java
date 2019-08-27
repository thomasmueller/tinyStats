package org.tinyStats.histogram.impl;

import org.tinyStats.histogram.LengthHistogram;

/**
 * An exact length histogram.
 */
public class ExactLengthHistogram implements LengthHistogram {

    private long[] counts = new long[12];

    @Override
    public void add(long hash, long length) {
        int logLength = Math.max(0, 63 - Long.numberOfLeadingZeros(length));
        int bucket = Math.min(11, (logLength + 2) / 3);
        counts[bucket]++;
    }

    @Override
    public int[] getHistogram() {
        long sum = 0;
        for (long x : counts) {
            sum += x;
        }
        int[] result = new int[12];
        for (int i = 0; i < counts.length; i++) {
            long c = counts[i];
            if (c > 0) {
                result[i] = Math.max(1, (int) (100 * c / sum));
            }
        }
        return result;
    }

}
