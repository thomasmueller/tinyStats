package org.tinyStats.countSketch;

import org.tinyStats.countSketch.impl.AMSSketch;
import org.tinyStats.countSketch.impl.CountMinMeanSketch;
import org.tinyStats.countSketch.impl.CountMinSketchPercent;
import org.tinyStats.countSketch.impl.int64.FrequentItemDetector64;
import org.tinyStats.countSketch.impl.int64.FrequentK64;
import org.tinyStats.countSketch.impl.int64.Majority64;

enum CountSketchType {
        FREQUENT_ITEM_DETECT_64 {
            @Override
            public FrequentItemDetector64 construct() {
                return new FrequentItemDetector64();
            }
        },
        COUNT_MEAN_MIN_SKETCH_5_16 {
            @Override
            public CountMinMeanSketch construct() {
                return new CountMinMeanSketch(5, 16);
            }
        },
        COUNT_MIN_SKETCH_PERCENT_5_16 {
            @Override
            public CountMinSketchPercent construct() {
                return new CountMinSketchPercent(5, 16);
            }
        },
        AMS_SKETCH_8_8 {
            @Override
            public AMSSketch construct() {
                return new AMSSketch(8, 8);
            }
        },
        MAJORITY_64 {
            @Override
            public Majority64 construct() {
                return new Majority64();
            }
        },
        FREQUENT_2_64 {
            @Override
            public FrequentK64 construct() {
                return new FrequentK64(2);
            }
        };

        public abstract CountSketch construct();

    }