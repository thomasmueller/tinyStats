package org.tinyStats.countSketch;

import org.tinyStats.countSketch.impl.AMSSketch;
import org.tinyStats.countSketch.impl.CountMinSketch;
import org.tinyStats.countSketch.impl.CountMinSketch2;
import org.tinyStats.countSketch.impl.int64.FrequentItemDetector64;
import org.tinyStats.countSketch.impl.int64.Frequent64;
import org.tinyStats.countSketch.impl.int64.Majority64;

enum CountSketchType {
        COUNT_SKETCH_64 {
            @Override
            public CountSketch construct() {
                return new FrequentItemDetector64();
            }
        },
        MAJORITY_64 {
            @Override
            public CountSketch construct() {
                return new Majority64();
            }
        },
        FREQUENT_2_64 {
            @Override
            public CountSketch construct() {
                return new Frequent64(2);
            }
        },
        AMS_SKETCH {
            @Override
            public AMSSketch construct() {
                return new AMSSketch(8, 8);
            }
        },
        COUNT_MIN_SKETCH2 {
            @Override
            public CountSketch construct() {
                return new CountMinSketch2();
            }
        },
        COUNT_MIN_SKETCH {
            @Override
            public CountSketch construct() {
                return new CountMinSketch();
            }
        };
        public abstract CountSketch construct();

    }