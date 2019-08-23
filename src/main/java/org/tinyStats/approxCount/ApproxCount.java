package org.tinyStats.approxCount;

/**
 * Count entries approximately.
 * 
 * A simple implementation can use a counter. An implementation that uses less
 * space (e.g. one byte) can rely on the hash to rarely repeat. That means, an
 * algorithm like HyperLogLog can be used, without having to care about adding
 * the same entry twice.
 */
public interface ApproxCount {
    
    /**
     * Add an entry. This method is fast.
     * 
     * @param hash a hash of the entry, or a random number if there is no entry. It
     *             is assumed each entry is only added once, which means likely the
     *             hash value will be different on each entry. Hash collisions are
     *             fine, but they should not appear more frequently than expected.
     */
    void add(long hash);

    /**
     * Remove an entry (optional). This method is fast. If this method is used, the
     * add and remove methods need to use hash values of the entry, otherwise there
     * is no way to guarantee an approximate counter will go to 0 when adding and
     * removing the same set of entries.
     * 
     * @param hash a hash of the entry (the same hash as used when adding the entry)
     */
    default void remove(long hash) {
        // no-op
    }
    
    /**
     * Whether the remove operation is supported.
     * 
     * @return true if it is
     */
    default boolean supportsRemove() {
        return false;
    }

    /**
     * Get the estimated count. This method might be a bit
     * slow (use floating point operations and loops).
     * 
     * @return the estimated number of entries
     */
    long estimate();

}
