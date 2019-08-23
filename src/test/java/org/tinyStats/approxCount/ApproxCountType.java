package org.tinyStats.approxCount;

import org.tinyStats.approxCount.impl.ApproxLinearCount1024;
import org.tinyStats.approxCount.impl.ApproxCount16;
import org.tinyStats.approxCount.impl.ApproxCount4;
import org.tinyStats.approxCount.impl.ApproxCount8;
import org.tinyStats.approxCount.impl.ExactCount;

public enum ApproxCountType {
    EXACT {
        @Override
        public ExactCount construct() {
            return new ExactCount();
        }
    },
    APPROX_LINEAR_1024 {
        @Override
        public ApproxLinearCount1024 construct() {
            return new ApproxLinearCount1024();
        }
    },
    APPROX_4 {
        @Override
        public ApproxCount4 construct() {
            return new ApproxCount4();
        }
    },
    APPROX_8 {
        @Override
        public ApproxCount8 construct() {
            return new ApproxCount8();
        }
    },
    APPROX_16 {
        @Override
        public ApproxCount16 construct() {
            return new ApproxCount16();
        }
    };

    public abstract ApproxCount construct();

}
