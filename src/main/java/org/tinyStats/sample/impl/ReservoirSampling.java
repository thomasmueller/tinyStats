package org.tinyStats.sample.impl;

import java.util.ArrayList;

import org.tinyStats.sample.RandomSample;
import org.tinyStats.util.Hash;

/**
 * Reservoir sampling. When adding more than 1 billion entries, new entries
 * replace existing entries slightly more often: with a fixed probability of 1:1
 * billion.
 *
 * @param <T>
 */
public class ReservoirSampling<T> implements RandomSample<T> {

    private final int resultSize;
    private ArrayList<T> list = new ArrayList<T>();
    private long count;

    public ReservoirSampling(int count) {
        this.resultSize = count;
    }

    @Override
    public void add(T obj) {
        count++;
        if (list.size() < resultSize) {
            list.add(obj);
            return;
        }
        long random = Hash.randomLong();
        int reduce = (int) Math.min(count, 1_000_000_000);
        int index = Hash.reduce((int) random, reduce);
        if (index < resultSize) {
            list.set(index, obj);
        }
    }

    @Override
    public Iterable<T> list() {
        return list;
    }

}
