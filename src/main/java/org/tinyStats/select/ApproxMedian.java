package org.tinyStats.select;

/**
 * Get the approximate median of a stream.
 *
 * Data entries don't need to be numbers: we only need a way to compare entries.
 * The result is guaranteed to be one of the input entries.
 *
 * @param <T> the type
 */
public interface ApproxMedian<T> {

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
