package org.tinyStats.countSketch.impl.int64;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;

/**
 * The "frequent" algorithm, which is similar to the "majority" algorithm, but
 * supports more than 1 entry.
 *
 * For each frequent entry, 64 bits are needed (32 bit for the counter, 32 bit
 * for the hash).
 *
 * The estimate is 1 for the most frequent element, 2 for the second,... and 0
 * otherwise.
 */
public class Frequent64 implements CountSketch {

    // upper 32 bits: count
    // lower 32 bits: hash
    private long[] state;

    public Frequent64(int count) {
        state = new long[count];
    }

    @Override
    public void add(long hash) {
        for (int i = 0; i < state.length; i++) {
            if ((int) state[i] == (int) hash) {
                state[i] += (1L << 32);
                return;
            }
        }
        for (int i = 0; i < state.length; i++) {
            if (state[i] >>> 32 == 0) {
                state[i] = (1L << 32) | (hash & 0xffffffffL);
                return;
            }
        }
        for (int i = 0; i < state.length; i++) {
            state[i] -= (1L << 32);
        }
    }

    @Override
    public long estimate(long hash) {
        long[] s = Arrays.copyOf(state, state.length);
        Arrays.sort(s);
        for (int i = 0; i < s.length; i++) {
            if ((int) s[i] == (int) hash) {
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public long estimateRepeatRate() {
        return 0;
    }

}
