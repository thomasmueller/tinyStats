package org.tinyStats.cardinality;

import org.tinyStats.cardinality.CardinalityEstimator;
import org.tinyStats.cardinality.impl.HyperBitBit;
import org.tinyStats.cardinality.impl.HyperLogLog;
import org.tinyStats.cardinality.impl.int64.HyperBitBit64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog64;
import org.tinyStats.cardinality.impl.int64.HyperLogLogTailCut64;
import org.tinyStats.cardinality.impl.int64.LinearCounting64;

public enum EstimatorType {

    LINEAR_COUNTING_64 {
        @Override
        public CardinalityEstimator construct() {
            return new LinearCounting64();
        }
    },
    HYPER_LOG_LOG_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog64();
        }
    },
    HYPER_BIT_BIT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperBitBit64();
        }
    },
    HYPER_LOG_LOG_TAILCUT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLogTailCut64();
        }
    },
    HYPER_BIT_BIT {
        @Override
        public CardinalityEstimator construct() {
            return new HyperBitBit();
        }
    },
    HYPER_LOG_LOG_1K {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog(1024);
        }
    };
    

    public abstract CardinalityEstimator construct();

}
