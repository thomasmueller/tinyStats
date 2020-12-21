package org.tinyStats.select;

/**
 * Get an approximate element of a stream, for example the approximate median,
 * or the approximate kth element.
 *
 * Data entries don't need to be numbers: we only need a way to compare entries.
 * The result is guaranteed to be one of the input entries.
 *
 * @param <T> the type
 */
public interface ApproxSelect<T> {

    /**
     * Add an entry.
     *
     * @param obj the object
     */
    void add(T obj);

    /**
     * Get the approximate median.
     *
     * @return the approximate median
     */
    T getApproxMedian();

}
