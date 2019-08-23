package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;

/**
 * An order-of-magnitude count approximation. It uses a state of 16 bits.
 *
 * It is similar to HyperBitBit. It uses a 8 bit counter, and 8 bit for flags.
 *
 * The estimation is often half or twice the real value.
 */
public class ApproxCount16 implements ApproxCount {

    private int data;

    @Override
    public void add(long hash) {
        int base = data >>> 8;
        if (base == 0) {
            if (data == 0xff) {
                data = 0x100;
                return;
            }
            data |= 1 << (hash & 0x7);
            return;
        }
        int id = (int) (hash & 0x7);
        int bit = 1 << id;
        int zeros = Long.numberOfLeadingZeros(hash);
        if (zeros >= base) {
            data |= bit;
            if ((data & 0xff) == 0xff && data != 0xffff) {
                data = (data & 0xff00) + 0x100;
            }
        }
    }

    @Override
    public long estimate() {
        if (data == 0) {
            return 0;
        }
        int base = data >>> 8;
        int bitCount = Long.bitCount(data & 0xff);
        if (base == 0) {
            // linear counting
            int m = 8;
            long est = (long) (1.4 * m * Math.log((double) m / (9 - bitCount)));
            return Math.max(1, est);
        }
        return (long) (
                131. / (((double) bitCount / (1L << (base + 1))) +
                (double) (8 - bitCount) / (1L << base)));
    }

}
