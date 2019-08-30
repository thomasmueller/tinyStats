package org.tinyStats.histogram.impl.int64;

import org.tinyStats.histogram.Histogram;
import org.tinyStats.util.Hash;

/**
 * A histogram that uses 64 bits of state (11 buckets of 5 bits each,
 * plus a 8 bits base counter; the highest bit is not used).
 *
 * Linear approximate counting is used.
 *
 * Initially, the base counter is 0, and adding an entry for a bucket increments
 * the count for the given bucket. If the maximum count for a bucket is reached,
 * the base counter is incremented, and values for all counters are divided by
 * 2. From then on, adding an entry for a bucket will only increment the counter
 * with a certain probability depending on the base counter (1/2, 1/4,...).
 *
 * Accuracy is usually within +/- 5, but sometimes +/- 10 for each bucket.
 */
public class ApproxHistogram11 implements Histogram {

    private long data;

    @Override
    public void add(int bucket) {
        int base = (int) (data & 0xff);
        long d = (int) (data >>> (8 + bucket * 5)) & 0x1f;
        if (d == 0x1f) {
            long result = Math.min(0xff, base + 1);
            for (int i = 0; i < 11; i++) {
                long old = (int) (data >>> (8 + i * 5)) & 0x1f;
                long smaller = old == 0 ? 0 : Math.max(1L, old >> 1);
                result += smaller << (8 + i * 5);
            }
            data = result;
            return;
        }
        if (base == 0 || d == 0) {
            data += 1L << (8 + bucket * 5);
            return;
        }
        long random = Hash.randomLong();
        int zeros = Long.numberOfLeadingZeros(random);
        if (zeros >= base) {
            data += 1L << (8 + bucket * 5);
        }
    }

    @Override
    public int[] getHistogram() {
        long[] counts = new long[11];
        int base = (int) (data & 0xff);
        long d = data >>> 8;
        long sum = 0;
        for (int i = 0; i < 11; i++) {
            long x = d & 0x1f;
            if (x > 1) {
                x <<= base;
            }
            d >>>= 5;
            counts[i] = x;
            sum += x;
        }
        int[] result = new int[11];
        for (int i = 0; i < 11; i++) {
            long c = counts[i];
            if (c > 0) {
                result[i] = Math.max(1, (int) (100 * c / sum));
            }
        }
        return result;
    }

}
