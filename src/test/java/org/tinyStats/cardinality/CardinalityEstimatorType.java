package org.tinyStats.cardinality;

import org.tinyStats.cardinality.CardinalityEstimator;
import org.tinyStats.cardinality.impl.HyperBitBit;
import org.tinyStats.cardinality.impl.HyperLogLog;
import org.tinyStats.cardinality.impl.int64.HyperBitBit64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog2Linear64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog2TailCut64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog3Linear64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog3TailCut64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog5Bit64;
import org.tinyStats.cardinality.impl.int64.HyperLogLog4TailCut64;
import org.tinyStats.cardinality.impl.int64.LinearCounting64;

public enum CardinalityEstimatorType {
    HYPER_BIT_BIT {
        @Override
        public CardinalityEstimator construct() {
            return new HyperBitBit();
        }
    },
    HYPER_LOG_LOG {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog(1024);
        }
    },
    HYPER_BIT_BIT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperBitBit64();
        }
    },
    HYPER_LOG_LOG_2_LINEAR_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog2Linear64();
        }
    },
    HYPER_LOG_LOG_2_TAILCUT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog2TailCut64();
        }
    },
    HYPER_LOG_LOG_3_LINEAR_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog3Linear64();
        }
    },
    HYPER_LOG_LOG_3_TAILCUT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog3TailCut64();
        }
    },
    HYPER_LOG_LOG_4_TAILCUT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog4TailCut64();
        }
    },
    HYPER_LOG_LOG_5_BIT_64 {
        @Override
        public CardinalityEstimator construct() {
            return new HyperLogLog5Bit64();
        }
    },
    LINEAR_COUNTING_64 {
        @Override
        public CardinalityEstimator construct() {
            return new LinearCounting64();
        }
    };

    public abstract CardinalityEstimator construct();

}
