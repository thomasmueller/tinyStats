package org.tinyStats.median.impl;

import java.util.ArrayList;
import java.util.Comparator;

import org.tinyStats.median.ApproxMedian;

/**
 * An improved variant of the remedian algorithm. See the papers "The Remedian -
 * A Robust Averaging Method for Large Data Sets" and "Further analysis of the
 * remedian algorithm".
 *
 * This implementation supports partially sorted and sorted input much better
 * than the original algorithm, at the cost of slightly higher memory usage,
 * even thought, still O(log n).
 *
 * A new, higher-level buffer is allocated only once the previous buffer has
 * size b^2. This only applies to the highest level; lower-level buffers are
 * re-used.
 */
public class RemedianPlus<T> implements ApproxMedian<T> {

    private final int base;
    private final Comparator<T> comparator;
    private final ArrayList<ArrayList<T>> lists = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param base       the maximum size of a buffer, except for the last one
     *                   (needs to be odd)
     * @param comparator the comparator, or null if entries implement Comparable<T>
     */
    public RemedianPlus(int base, Comparator<T> comparator) {
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
            if (index == lists.size() - 1) {
                if (list.size() < base * base) {
                    return;
                }
                list.sort(comparator);
                ArrayList<T> list2 = new ArrayList<>();
                for (int j = 0; j < base; j++) {
                    int i = (j * base) + (base / 2);
                    obj = list.get(i);
                    list2.add(obj);
                }
                list.clear();
                lists.add(list2);
                return;
            }
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
        list.sort(comparator);
        return list.get(list.size() / 2);
    }

}
