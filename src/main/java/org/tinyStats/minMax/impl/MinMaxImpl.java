package org.tinyStats.minMax.impl;

import org.tinyStats.minMax.MinMax;

public class MinMaxImpl<T extends Comparable<? super T>> implements MinMax<T> {

    private T min, max;

    @Override
    public void add(T obj) {
        if (min == null || min.compareTo(obj) > 0) {
            min = obj;
        }
        if (max == null || max.compareTo(obj) < 0) {
            max = obj;
        }
    }

    @Override
    public T getMin() {
        return min;
    }

    @Override
    public T getMax() {
        return max;
    }

}
