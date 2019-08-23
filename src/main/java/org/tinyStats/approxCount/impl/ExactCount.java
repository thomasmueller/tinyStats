package org.tinyStats.approxCount.impl;

import org.tinyStats.approxCount.ApproxCount;

/**
 * An exact counter.
 */
public class ExactCount implements ApproxCount {

    private long count;

    @Override
    public void add(long hash) {
        count++;
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
