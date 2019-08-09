package org.tinyStats.cardinality;

/**
 * Allows to estimate the cardinality (number of distinct entries) of a set.
 */
public interface CardinalityEstimator {
    
    /**
     * Add an entry to the set. This method is fast.
     * 
     * @param hash the hash of the entry (64 bit; needs to come from a high-quality
     *             hash function such as Murmur hash)
     */
    void add(long hash);
    
    /**
     * Get the estimated cardinality (number of distinct entries). This method might be a bit
     * slow (use floating point operations and loops).
     * 
     * @return the estimated number of distinct entries
     */
    long estimate();

}
