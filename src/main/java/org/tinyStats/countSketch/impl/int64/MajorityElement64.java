package org.tinyStats.countSketch.impl.int64;

import org.tinyStats.countSketch.CountSketch;

/**
 * If the stream has a majority element (an element that occurs more often than
 * all other elements combined), then this simple algorithm is able to identify
 * it. Otherwise, it will identify some random element. Does not detect where
 * the _is_ a majority element.
 * 
 * The estimate is 1 for the identified element, and 0 otherwise. 
 */
public class MajorityElement64 implements CountSketch {

    private long state;
    
    @Override
    public void add(long hash) {
        int oldHash = (int) state;
        if (oldHash  == 0) {
            state = (1L << 32) + (int) hash;
            return;
        }
        if (oldHash == (int) hash) {
            state += (1L << 32);
        } else {
            state -= (1L << 32);
        }
    }

    @Override
    public long estimate(long hash) {
        return (int) state == (int) hash ? 1 : 0;
    }

}
