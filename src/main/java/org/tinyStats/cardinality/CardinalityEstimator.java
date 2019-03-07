package org.tinyStats.cardinality;

public interface CardinalityEstimator {
    void add(long hash);
    long estimate();
}
