package org.tinyStats.countSketch.impl;

import org.tinyStats.countSketch.CountSketch;

/**
 * Alon-Matias-Szegedy (AMS) sketches, from "The space complexity of
 * approximating the frequency moments"
 * 
 * See also "Using the AMS Sketch to estimate inner products"
 * (can be used to  estimate join sizes between tables, see 
 * https://github.com/twitter/algebird/issues/563
 * 
 * https://github.com/mayconbordin/streaminer/blob/master/src/main/java/org/streaminer/stream/frequency/AMSSketch.java
 */
public class AMSSketch implements CountSketch {

    @Override
    public void add(long hash) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public long estimate(long hash) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long estimateRepeatRate() {
        // TODO Auto-generated method stub
        return 0;
    }

}
