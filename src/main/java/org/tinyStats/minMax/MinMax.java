package org.tinyStats.minMax;

/**
 * Get the minimum / maximum of a  stream of values.
 *
 * @param <T> the type
 */
public interface MinMax<T> {

    /**
     * Add an entry.
     *
     * @param obj the object to add
     */
    void add(T obj);

    /**
     * Get the minimum (smallest) added object.
     *
     * @return the smallest
     */
    T getMin();

    /**
     * Get the maximum (largest) added object.
     *
     * @return the largest
     */
    T getMax();

}
