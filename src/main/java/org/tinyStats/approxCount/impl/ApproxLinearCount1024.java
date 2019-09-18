package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;

/**
 * An approximate counter. Resolution is 1024. When adding and then removing the
 * same entries, the count goes back to 0.
 *
 * See also the paper "Approximate Counting: A detailed analysis" by P.
 * Flajolet.
 *
 * Relative accuracy is relatively bad if there are few entries, and gets better
 * with more entries.
 */
public class ApproxLinearCount1024 implements ApproxCount {

    private static final long RESOLUTION_SHIFT = 10;
    private static final long RESOLUTION = 1L << RESOLUTION_SHIFT;
    private static final long MASK = RESOLUTION - 1;
    private long count;

    @Override
    public void add(long hash) {
        if ((hash & MASK) == 0) {
            count++;
        }
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Override
    public void remove(long hash) {
        if ((hash & MASK) == 0) {
            count--;
        }
    }

    @Override
    public long estimate() {
        return count * RESOLUTION;
    }

}
