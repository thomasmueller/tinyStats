package org.tinyStats.countDistinctSketch;

/**
 * Allows to estimate the number of distinct entries (cardinality) of a specific
 * entry of a set.
 */
public interface CountDistinctSketch {

    /**
     * Add a key-value pair to the set. This method is fast.
     *
     * @param keyHash   the hash of the key (64 bit; needs to come from a
     *                  high-quality hash function such as Murmur hash)
     * @param valueHash the hash of the value (64 bit; needs to come from a
     *                  high-quality hash function such as Murmur hash)
     */
    void add(long keyHash, long valueHash);

    /**
     * For a specific entry, get the estimated cardinality (number of distinct
     * values). This method might be a bit slow (use floating point operations and
     * loops).
     *
     * @param keyHash the hash of the key (64 bit; needs to come from a high-quality
     *                hash function such as Murmur hash)
     * @return 0 if the entry is likely not frequent, otherwise some number > 0,
     *         where the exact meaning depends on the algorithm
     */
    long estimate(long keyHash);

}
