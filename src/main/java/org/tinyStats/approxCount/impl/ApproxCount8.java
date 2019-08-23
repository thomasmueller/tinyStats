package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;
import org.tinyStats.util.Hash;

/**
 * An order-of-magnitude count approximation. It uses a state of 8 bits, and can
 * count up to about 20 billion in theory.
 *
 * It is similar to HyperBitBit. It uses a 5 bit level counter (0..31), and 3
 * bit for flags.
 *
 * The estimation is often half or twice the real value.
 */
public class ApproxCount8 implements ApproxCount {

    private int data;

    @Override
    public void add(long hash) {
        int base = data >>> 3;
        if (base == 0) {
            if (data == 0x7) {
                data = 1 << 3;
                return;
            }
            data |= 1 << Hash.reduce((int) hash, 3);
            return;
        }
        int zeros = Long.numberOfTrailingZeros(hash);
        if (zeros >= base) {
            if ((data & 0x7) == 0x7 && (data >>> 3) < 31) {
                data = (data ^ 0x7) + (1 << 3);
                return;
            }
            int id = Hash.reduce((int) hash, 3);
            int bit = 1 << id;
            data |= bit;
        }
    }

    @Override
    public long estimate() {
        if (data == 0) {
            return 0;
        }
        int base = data >>> 3;
        int bitCount = Long.bitCount(data & 0x7);
        if (base == 0) {
            // linear counting
            int m = 3;
            long est = (long) (1.7 * m * Math.log((double) m / (4 - bitCount)));
            return Math.max(1, est);
        }
        return (long) (
                14. / (((double) bitCount / (1L << (base + 1))) +
                (double) (3 - bitCount) / (1L << base)));
    }

}
