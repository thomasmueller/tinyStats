package org.tinyStats.median;

import org.tinyStats.median.impl.RemedianSimple;
import org.tinyStats.median.impl.RemedianPlus;

public enum MedianType {
    REMEDIAN_SIMPLE {
        @Override
        public <T> ApproxMedian<T> construct(int base) {
            return new RemedianSimple<T>(base, null);
        }
    },
    REMEDIAN_PLUS {
        @Override
        public <T> ApproxMedian<T> construct(int base) {
            return new RemedianPlus<T>(base, null);
        }
    };
    public abstract <T> ApproxMedian<T> construct(int base);

}
