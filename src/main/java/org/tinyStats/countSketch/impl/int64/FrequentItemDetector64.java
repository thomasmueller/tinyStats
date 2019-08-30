package org.tinyStats.countSketch.impl.int64;

import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.util.Hash;

/**
 * This class can detect whether a set of entries has frequent items. If it has,
 * then estimateRepeatRate returns a high value (about 60..100). If not, then a
 * lower value is returned (0..59).
 *
 * Internally it keeps a histogram of 14 buckets, and increments one bucket when
 * an entry is added. It returns the percentage of the bucket with the highest
 * frequency. If near 100, then that is likely just one entry that appears more
 * than 50% of the time, but in rare cases (1 / 196) two entries that just
 * happen to be mapped to the same bucket.
 *
 * It can't be used to detect which entries are the most frequent one.
 */
public class FrequentItemDetector64 implements CountSketch {

    private long data;

    @Override
    public void add(long hash) {
        int base = (int) (data >>> 56);
        int bucket = Hash.reduce((int) hash, 14);
        long d = (int) (data >>> (bucket * 4)) & 0xf;
        if (d == 0xf) {
            data = (Math.min(0xffL, base + 1) << 56)
                    | ((data & 0xeeeeeeeeeeeeeeL) >> 1);
            return;
        }
        if (base == 0) {
            data += 1L << (bucket * 4);
            return;
        }
        long random = Hash.randomLong();
        int zeros = Long.numberOfTrailingZeros(random);
        if (zeros >= base) {
            data += 1L << (bucket * 4);
        }
    }

    @Override
    public long estimate(long hash) {
        return 0;
    }

    @Override
    public long estimateRepeatRate() {
        long d = data;
        long sum = 0;
        long max = 0;
        for (int i = 0; i < 14; i++) {
            long c = d & 0xf;
            sum += c;
            max = Math.max(max, c);
            d >>>= 4;
        }
        return 100 * max / sum;
    }

}
