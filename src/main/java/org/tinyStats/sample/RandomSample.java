package org.tinyStats.sample;

/**
 * Allow getting a random sample of entries from a stream.
 *
 * @param <T> the type
 */
public interface RandomSample<T> {

    /**
     * Add an entry.
     *
     * @param obj the entry
     */
    void add(T obj);

    /**
     * Get a random sample. The list is unordered and possibly is zero. It may
     * contain the same object multiple times.
     *
     * @return the list
     */
    Iterable<T> list();
}
