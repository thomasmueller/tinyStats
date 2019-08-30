package org.tinyStats.histogram;

/**
 * A histogram. It can estimate the percentages of entries in buckets.
 */
public interface Histogram {

    /**
     * Add an entry.
     *
     * @param bucket the bucket
     */
    void add(int bucket);

    /**
     * Get the histogram. An array is returned that contains the estimated
     * percentages of entries in the buckets (0..100).
     *
     * The sum of the entries in the result may not match 100 in all cases due to
     * rounding errors.
     *
     * If a bucket has one or more entries, then it may not return 0 for that
     * bucket. (to allow to detect if there is an entry).
     *
     * @return an array with percentages
     */
    int[] getHistogram();

}
