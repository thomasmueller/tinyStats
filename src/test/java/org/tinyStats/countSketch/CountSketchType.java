package org.tinyStats.countSketch;

import org.tinyStats.countSketch.impl.AMSSketch;
import org.tinyStats.countSketch.impl.CountMinSketch;
import org.tinyStats.countSketch.impl.int64.FrequentItemDetector64;
import org.tinyStats.countSketch.impl.int64.Frequent64;
import org.tinyStats.countSketch.impl.int64.Majority64;

enum CountSketchType {
        FREQUENT_ITEM_DETECT_64 {
            @Override
            public CountSketch construct() {
                return new FrequentItemDetector64();
            }
        },
        COUNT_MIN_SKETCH_4_16 {
            @Override
            public CountSketch construct() {
                return new CountMinSketch(4, 16);
            }
        },
        AMS_SKETCH_8_8 {
            @Override
            public AMSSketch construct() {
                return new AMSSketch(8, 8);
            }
        };
//        MAJORITY_64 {
//            @Override
//            public CountSketch construct() {
//                return new Majority64();
//            }
//        },
//        FREQUENT_2_64 {
//            @Override
//            public CountSketch construct() {
//                return new Frequent64(2);
//            }
//        },
        public abstract CountSketch construct();

    }