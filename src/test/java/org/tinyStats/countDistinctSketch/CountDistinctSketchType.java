package org.tinyStats.countDistinctSketch;

import org.tinyStats.countDistinctSketch.impl.HyperLogLogMinMeanSketch;

public enum CountDistinctSketchType {

    HYPER_LOG_LOG_64_SKETCH_5_16 {
        @Override
        public CountDistinctSketch construct() {
            return new HyperLogLogMinMeanSketch(5, 16);
        }
    };

    public abstract CountDistinctSketch construct();

}
