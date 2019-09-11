package org.tinyStats.median;

/**
 * Get the approximate median of a stream.
 *
 * Data entries don't need to be numbers: we only need a way to compare entries.
 * The result is guaranteed to be one of the input entries.
 *
 * @param <T> the type
 */
public interface ApproxMedian<T> {

    void add(T obj);

    T getApproxMedian();

}
