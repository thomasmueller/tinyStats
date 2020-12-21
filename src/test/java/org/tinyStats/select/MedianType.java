package org.tinyStats.select;

import org.tinyStats.select.ApproxMedian;
import org.tinyStats.select.impl.RemedianPlus;
import org.tinyStats.select.impl.RemedianSimple;

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
