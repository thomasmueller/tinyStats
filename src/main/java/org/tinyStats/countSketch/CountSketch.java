package org.tinyStats.countSketch;

/**
 * Allows to estimate the number of entries (count) of a specific entry of a
 * set.
 */
public interface CountSketch {

    /**
     * Add an entry to the set. This method is fast.
     *
     * @param hash the hash of the entry (64 bit; needs to come from a high-quality
     *             hash function such as Murmur hash)
     */
    void add(long hash);

    /**
     * For a specific entry, get the estimated count (number of times the same entry
     * was added). This method might be a bit slow (use floating point operations
     * and loops).
     *
     * @param hash the hash of the key
     * @return 0 if the entry is likely not frequent, otherwise some number > 0,
     *         where the exact meaning depends on the algorithm
     */
    long estimate(long hash);

    /**
     * Estimate the repeat rate (also called second frequency moment, F2, or
     * homogeneity). A low value means each distinct entry appears roughly the same
     * number of times, and a high value means the distribution is very skewed.
     *
     * @return the repeat rate, or 0 if not known
     */
    long estimateRepeatRate();

}