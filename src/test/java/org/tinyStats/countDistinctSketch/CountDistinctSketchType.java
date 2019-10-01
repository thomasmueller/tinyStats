package org.tinyStats.countDistinctSketch;

import org.tinyStats.countDistinctSketch.impl.HyperLogLogMinSketch;

public enum CountDistinctSketchType {

    HYPER_LOG_LOG_64_SKETCH_4_16 {
        @Override
        public CountDistinctSketch construct() {
            return new HyperLogLogMinSketch(4, 16);
        }
    };

    public abstract CountDistinctSketch construct();

}
