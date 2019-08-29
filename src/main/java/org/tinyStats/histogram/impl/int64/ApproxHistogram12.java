package org.tinyStats.histogram.impl.int64;

import org.tinyStats.histogram.Histogram;

/**
 * A histogram that uses 64 bits of state (12 buckets of 5 bits each,
 * plus a 4 bits base counter).
 *
 * Linear approximate counting is used. The last roughly 33 - 400 billion
 * entries are counted only.
 *
 * Initially, the base counter is 0, and adding an entry for a bucket increments
 * the count for the given bucket. If the maximum count for a bucket is reached,
 * the base counter is incremented, and values for all counters are divided by
 * 2. From then on, adding an entry for a bucket will only increment the counter
 * with a certain probability depending on the base counter (1/4, 1/16,...).
 *
 * Accuracy is usually within +/- 5, but sometimes +/- 15 for each bucket.
 */
public class ApproxHistogram12 implements Histogram {

    private long data;

    @Override
    public void add(long hash, int bucket) {
        int base = (int) (data & 0xf);
        long d = (int) (data >>> (4 + bucket * 5)) & 0x1f;
        if (d == 0x1f) {
            long result = Math.min(15, base + 1);
            for (int i = 0; i < 12; i++) {
                long old = (int) (data >>> (4 + i * 5)) & 0x1f;
                long smaller = old == 0 ? 0 : Math.max(1L, old >> 2);
                result += smaller << (4 + i * 5);
            }
            data = result;
            return;
        }
        if (base == 0 || d == 0) {
            data += 1L << (4 + bucket * 5);
            return;
        }
        int zeros = Long.numberOfLeadingZeros(hash);
        if (zeros >= base * 2) {
            data += 1L << (4 + bucket * 5);
        }
    }

    @Override
    public int[] getHistogram() {
        long[] counts = new long[12];
        int base = (int) (data & 0xf);
        long d = data >>> 4;
        long sum = 0;
        for (int i = 0; i < 12; i++) {
            long x = d & 0x1f;
            if (x > 1) {
                x <<= (2 * base);
            }
            d >>>= 5;
            counts[i] = x;
            sum += x;
        }
        int[] result = new int[12];
        for (int i = 0; i < 12; i++) {
            long c = counts[i];
            if (c > 0) {
                result[i] = Math.max(1, (int) (100 * c / sum));
            }
        }
        return result;
    }

}
