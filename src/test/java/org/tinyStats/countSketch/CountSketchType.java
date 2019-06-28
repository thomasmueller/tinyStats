package org.tinyStats.countSketch;

import org.tinyStats.countSketch.impl.PartList;
import org.tinyStats.countSketch.impl.CountMinSketch2;
import org.tinyStats.countSketch.impl.CountMinSketch;
import org.tinyStats.countSketch.impl.Reinforce;

enum CountSketchType {
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
        },
        PART_LIST {
            @Override
            public CountSketch construct() {
                return new PartList();
            }
        },
        REINFORCE {
            @Override
            public CountSketch construct() {
                return new Reinforce();
            }
        };
        public abstract CountSketch construct();

    }