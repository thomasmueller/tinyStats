package org.tinyStats.histogram;

import org.tinyStats.histogram.impl.ExactLengthHistogram;
import org.tinyStats.histogram.impl.int64.LinearApproxLengthHistogram;

public enum LengthHistogramType {
    APPROX_LENGTH_HISTOGRAM {
        @Override
        public LinearApproxLengthHistogram construct() {
            return new LinearApproxLengthHistogram();
        }
    },
    EXACT_LENGTH_HISTOGRAM {
        @Override
        public ExactLengthHistogram construct() {
            return new ExactLengthHistogram();
        }
    };
    public abstract LengthHistogram construct();

}
