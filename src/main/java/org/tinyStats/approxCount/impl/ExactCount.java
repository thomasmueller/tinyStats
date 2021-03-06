package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;

/**
 * An exact counter, using 64 bits of state.
 */
public class ExactCount implements ApproxCount {

    private long count;

    @Override
    public void add(long hash) {
        count++;
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Override
    public void remove(long hash) {
        count--;
    }

    @Override
    public long estimate() {
        return count;
    }

}
