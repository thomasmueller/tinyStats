package org.tinyStats.cardinality.impl.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation for small cardinalities. It is like a Bloom filter
 * with 64 entries and one hash per key.
 * 
 * The implementation is very simple and fast. It is very good for cardinalities
 * up to about 200, but is not usable for higher cardinalities, when most bits
 * are set. If it reports a cardinality of 512, it means "probably more than
 * 300".
 */
public class LinearCounting64 implements CardinalityEstimator {
    
    /**
     * The highest cardinality reported.
     */
    public static long MAX_REPORTED = 512;
    
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
        return data |= 1L << hash;
    }
    
    static long estimate(long data) {
        int x = Long.bitCount(data);
        int b = 64, m = 1;
        long est = (long) (-(b / m) * Math.log(1. - (double) x / b));
        return Math.min(est, MAX_REPORTED);
    }

}
