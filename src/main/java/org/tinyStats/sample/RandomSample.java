package org.tinyStats.sample;

public interface RandomSample<T> {
    void add(T obj);
    Iterable<T> list();
}
