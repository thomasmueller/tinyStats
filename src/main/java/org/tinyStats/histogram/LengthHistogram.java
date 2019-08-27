package org.tinyStats.histogram;

/**
 * A histogram on the length.
 *
 * It can estimate the percentages of entries with a certain length, where we
 * care about entries of size 1, 8, 64, 512, 4096,... (8^n). The result should have at
 * least 12 buckets (up to length 8 billion).
 *
 * The sum of the entries in the result may not match 100 in all cases due to
 * rounding errors.
 *
 * If a bucket has one entry, then it may not return 0 for that bucket.
 */
public interface LengthHistogram {

    /**
     * Add an entry.
     *
     * @param hash a hash of the entry, or a random number if there is no entry. It
     *             is assumed each entry is only added once, which means likely the
     *             hash value will be different on each entry. Hash collisions are
     *             fine, but they should not appear more frequently than expected.
     * @param length the length
     */
    void add(long hash, long length);

    /**
     * Get the histogram. An array is returned that contains the estimated
     * percentages of entries with length approximately 1, 8, 64, and so on.
     *
     * It is expected that if there is one entry or more entries of a certain size,
     * then the estimation of that bucket is 1, not 0 (to allow to detect if there
     * is an entry of a certain size).
     *
     * @return an array with percentages
     */
    int[] getHistogram();

}
