package org.tinyStats.histogram;

import org.tinyStats.histogram.impl.ExactHistogram;
import org.tinyStats.histogram.impl.int64.ApproxHistogram11;

public enum HistogramType {
    APPROX_11 {
        @Override
        public ApproxHistogram11 construct() {
            return new ApproxHistogram11();
        }
    },
    EXACT_11 {
        @Override
        public ExactHistogram construct() {
            return new ExactHistogram(11);
        }
    };
    public abstract Histogram construct();

}
