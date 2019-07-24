package org.tinyStats.cardinality.impl.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperLogLog algorithm.
 * 
 * Uses 12 counters of 5 bits each, so 4 bits are unused.
 */
public class HyperLogLog5Bit64 implements CardinalityEstimator {

    private long data;
    
    @Override
    public void add(long hash) {
        data = add(data, hash);
    }
    
    @Override
    public long estimate() {
        return estimate(data);
    }

    static long add(long data, long hash) {
        int z = Long.numberOfLeadingZeros(hash);
        if (z > 0) {
            int i = (int) (hash & 15);
            if (i < 12) {
                int shift = 5 * i;
                long old = (data >>> shift) & 0x1f;
                long m = Math.max(z,  old) & 0x1f;
                if (z > old) {
                    data = (data & ~(0x1fL << shift)) | (m << shift);
                }
            }
        }
        return data;
    }

    static long estimate(long data) {
        double sum = 0;
        long x = data;
        int countZero = 0;
        for (int i = 0; i < 12; i++) {
            long n = x & 0x1f;
            countZero += n == 0 ? 1 : 0;
            sum += 1. / (1L << (1 + n));
            x >>>= 5;
        }
        double est;
        if (countZero > 0) {
            // linear counting
            int m = 12;
            // est = 2.1 * m * Math.log((double) m / countZero);
            est = 1.9 * m * Math.log((double) m / countZero);
        } else {
            return (long) (1. / sum * 0.876 * 12 * 12);
        }
        return Math.max(1, (long) est);
    }
    
}
