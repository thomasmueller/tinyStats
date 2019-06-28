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
     * @return the estimated number of times the entry was added
     */
    long estimate(long hash);
}