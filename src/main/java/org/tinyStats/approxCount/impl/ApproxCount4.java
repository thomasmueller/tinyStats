package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;

/**
 * An order-of-magnitude count approximation. It uses a state of 4 bits, and can
 * count up to 4^16 (4'294'967'296).
 *
 * See also the paper "Approximate Counting: A detailed analysis" by P.
 * Flajolet.
 *
 * The estimation is often more than 10 times to small or too large.
 */
public class ApproxCount4 implements ApproxCount {

    private byte data;

    @Override
    public void add(long hash) {
        int zeros = Long.numberOfLeadingZeros(hash);
        if (zeros >= (2 * (1 + data))) {
            data = (byte) Math.min(15, data + 1);
        }
    }

    @Override
    public long estimate() {
        long result = 1L << (2 * data);
        result += result >>> 2;
        return result;
    }

}
