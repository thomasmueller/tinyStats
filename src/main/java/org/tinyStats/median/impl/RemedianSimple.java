package org.tinyStats.median.impl;

import java.util.ArrayList;
import java.util.Comparator;

import org.tinyStats.median.ApproxMedian;

/**
 * The remedian algorithm. See the papers "The Remedian - A Robust Averaging
 * Method for Large Data Sets".
 *
 * The median of the highest-level buffer is used. This works well for unsorted
 * data, but not so well for partially or fully sorted data, where the result
 * can be quite far from the real median.
 */
public class RemedianSimple<T> implements ApproxMedian<T> {

    private final int base;
    private final Comparator<T> comparator;
    private final ArrayList<ArrayList<T>> lists = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param base       the maximum size of a buffer (needs to be odd)
     * @param comparator the comparator, or null if entries implement Comparable<T>
     */
    public RemedianSimple(int base, Comparator<T> comparator) {
        if ((base & 1) == 0) {
            throw new IllegalArgumentException("Base needs to be odd for median to work well");
        }
        this.base = base;
        this.comparator = comparator;
    }

    @Override
    public String toString() {
        return lists.toString();
    }

    @Override
    public void add(T obj) {
        for (int index = 0;; index++) {
            if (lists.size() <= index) {
                lists.add(new ArrayList<>());
            }
            ArrayList<T> list = lists.get(index);
            list.add(obj);
            if (list.size() < base) {
                return;
            }
            list.sort(comparator);
            int i = base / 2;
            obj = list.get(i);
            list.clear();
        }
    }

    @Override
    public T getApproxMedian() {
        if (lists.isEmpty()) {
            return null;
        }
        ArrayList<T> list = lists.get(lists.size() - 1);
        return list.get(list.size() / 2);
    }

}
