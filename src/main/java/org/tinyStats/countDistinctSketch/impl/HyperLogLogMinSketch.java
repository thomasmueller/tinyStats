package org.tinyStats.countDistinctSketch.impl;

import org.tinyStats.countDistinctSketch.CountDistinctSketch;

/**
 * The combination of HyperLogLog and CountMinSketch.
 */
public class HyperLogLogMinSketch implements CountDistinctSketch {

    @Override
    public void add(long keyHash, long valueHash) {
        // TODO
    }

    @Override
    public long estimate(long keyHash) {
        // TODO
        return 0;
    }

    @Override
    public long estimateRepeatRate() {
        // TODO
        return 0;
    }

}
