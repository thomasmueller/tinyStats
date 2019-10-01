package org.tinyStats.countDistinctSketch.impl;

import org.tinyStats.cardinality.impl.int64.HyperLogLog2Linear64;
import org.tinyStats.countDistinctSketch.CountDistinctSketch;
import org.tinyStats.util.Hash;

/**
 * The combination of HyperLogLog and CountMinSketch.
 */
public class HyperLogLogMinSketch implements CountDistinctSketch {

    private int shift, m, k;
    private final long[][] data;
    private long countDistinct;

    /**
     * Create a count min sketch instance.
     *
     * @param m the number of buckets per hash function (must be a power of 2)
     * @param k the number of hash functions
     */
    public HyperLogLogMinSketch(int k, int m) {
        if (Integer.bitCount(m) != 1) {
            throw new IllegalArgumentException("Must be a power of 2: " + m);
        }
        this.shift = Integer.bitCount(m - 1);
        this.m = m;
        this.k = k;
        data = new long[k][m];
    }

    protected long countDistinct(long data, long hash) {
        return HyperLogLog2Linear64.add(data, hash);
    }

    @Override
    public void add(long keyHash, long valueHash) {
        countDistinct = countDistinct(countDistinct, Hash.hash64(keyHash, valueHash));
        for (int i = 0; i < k; i++) {
            int index = (int) (keyHash & (m - 1));
            long old = data[i][index];
            data[i][index] = countDistinct(old, valueHash);
            keyHash >>>= shift;
        }
    }

    protected long estimateDistinct(long data) {
        return HyperLogLog2Linear64.estimate(data);
    }

    @Override
    public long estimate(long keyHash) {
//        long minAll = Long.MAX_VALUE;
//        for (int i = 0; i < k; i++) {
//            for (int j = 0; j < m; j++) {
//                long x = data[i][j];
//                minAll = Math.min(minAll, estimateDistinct(x));
//            }
//        }
        long estimate = estimateDistinct(countDistinct);
        long min = Long.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            long x = data[i][(int) (keyHash & (m - 1))];
            min = Math.min(min, estimateDistinct(x));
            keyHash >>>= shift;
        }
//        return 100 * (min - minAll) / estimate;
        return min;
    }

    @Override
    public long estimateRepeatRate() {
        // TODO
        return 0;
    }

}
