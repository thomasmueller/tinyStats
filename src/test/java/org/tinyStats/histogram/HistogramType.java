package org.tinyStats.histogram;

import org.tinyStats.histogram.impl.ExactHistogram;
import org.tinyStats.histogram.impl.int64.ApproxHistogram12;

public enum HistogramType {
    APPROX_12 {
        @Override
        public ApproxHistogram12 construct() {
            return new ApproxHistogram12();
        }
    },
    EXACT_12 {
        @Override
        public ExactHistogram construct() {
            return new ExactHistogram(12);
        }
    };
    public abstract Histogram construct();

}
