package org.tinyStats.cardinality.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperLogLog algorithm.
 * 
 * Uses 12 counters of 5 bits each, so 4 bits are unused.
 */
public class HyperLogLog64 implements CardinalityEstimator {

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
        int z = Long.numberOfLeadingZeros(hash) - 2;
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
        double m = 12;
        double am = 0.876;
        // 16: 0.673; 32: 0.697; 64: 0.709
        double sum = 0;
        long x = data;
//System.out.println(Long.toHexString(x));            
        for (int i = 0; i < 12; i++) {
            sum += 1. / (1L << (3 + (x & 0x1f)));
            x >>>= 5;
        }
        return (long) (1. / sum * am * m * m);
    }
    
}
