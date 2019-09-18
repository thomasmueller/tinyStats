package org.tinyStats.cardinality.impl;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperBitBit algorithm as described in a
 * presentation by Robert Sedgewick:
 * https://www.cs.princeton.edu/~rs/talks/AC11-Cardinality.pdf
 */
public class HyperBitBit implements CardinalityEstimator {

    private int lgN = 5;
    private long sketch = 0, sketch2 = 0;

    @Override
    public void add(long hash) {
        int r = Long.numberOfTrailingZeros(hash);
        if (r > lgN) {
            int k = (int) (hash >>> (64 - 6));
            sketch |= 1L << k;
            if (r > lgN + 1) {
                sketch2 |= 1L << k;
            }
            if (Long.bitCount(sketch) > 31) {
                sketch = sketch2;
                sketch2 = 0;
                lgN++;
            }
        }
    }

    @Override
    public long estimate() {
        // return (long) (Math.pow(2, lgN + 5.4 + Long.bitCount(sketch) / 32.0));
        return (long) (Math.pow(2, lgN + 5.15 + Long.bitCount(sketch) / 32.0));
    }

}
