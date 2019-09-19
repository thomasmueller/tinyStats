package org.tinyStats.cardinality.impl;

import org.tinyStats.cardinality.CardinalityEstimator;

public class HyperLogLog implements CardinalityEstimator {

    private final int m;
    private byte[] counters;
    private final double am;

    public HyperLogLog(int m) {
        if (m < 16) {
            throw new IllegalArgumentException("Must be >= 16, is " + m);
        }
        if (Integer.bitCount(m) != 1) {
            throw new IllegalArgumentException("Must be a power of 2, is " + m);
        }
        this.m = m;
        switch (m) {
        case 32:
            am = 0.697;
            break;
        case 64:
            am = 0.709;
            break;
        default:
            am = 0.7213 / (1.0 + 1.079 / m);
        }
        this.counters = new byte[m];
    }

    @Override
    public void add(long hash) {
        int i = (int) (hash & (m - 1));
        counters[i] = (byte) Math.max(counters[i], 1 + Long.numberOfLeadingZeros(hash));
    }

    @Override
    public long estimate() {
        double sum = 0;
        int countZero = 0;
        for(int c : counters) {
            countZero += c == 0 ? 1 : 0;
            sum += 1. / (1L << (c & 0xff));
        }
        long est = (long) (1. / sum * am * m * m);
        if (est <= 5 * m && countZero > 0) {
            // linear counting
            est = (long) (m * Math.log((double) m / countZero));
        }
        return Math.max(1, est);
    }

}
