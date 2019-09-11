package org.tinyStats.minMax;

/**
 * Get the minimum / maximum of a  stream of values.
 *
 * @param <T> the type
 */
public interface MinMax<T> {

    void add(T obj);

    T getMin();

    T getMax();

}
