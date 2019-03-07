package org.tinyStats.cardinality.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperBitBit algorithm as described in a
 * presentation by Robert Sedgewick:
 * https://www.cs.princeton.edu/~rs/talks/AC11-Cardinality.pdf
 * but using only 64 bits of state.
 * 
 * It is very space saving, fast, and relatively simple. But unlike regular
 * HyperLogLog, it is quite "order-dependent": adding the same entry multiple
 * times can change the internal state, and so increase the estimation. The
 * effect depends on the order the entries are added. Adding the same set 10
 * times can increase the estimation by about 40%.
 */
public class HyperBitBit64 implements CardinalityEstimator {

    private final static long SK_MASK = (1L << 29) - 1;
    
    private long data;

    @Override
    public void add(long hash) {
        // 0..5: lgN; (>>6) 6..34: sk; (>>35) 35..63
        int r = Long.numberOfLeadingZeros(hash);
        int lgN = (int) (data & 31);
        if (r > lgN) {
            long sketch = (data >>> 6) & SK_MASK;
            long sketch2 = (data >>> (6 + 29)) & SK_MASK;
            sketch = (sketch | (1L << hash)) & SK_MASK;
            if (r > lgN + 1) {
                sketch2 |= (sketch2 | (1L << hash)) & SK_MASK;
            }
            if (Long.bitCount(sketch) > 14) {
                sketch = sketch2;
                sketch2 = 0;
                lgN++;
            }
            data = (sketch << 6) | (sketch2 << (6 + 29)) | (lgN & 31);
        }
    }

    @Override
    public long estimate() {
        int lgN = (int) (data & 31);
        long sketch = (data >>> 6) & SK_MASK;
        return (long) (Math.pow(2, lgN + 5.1 + Long.bitCount(sketch) / 14.0));
    }

}
