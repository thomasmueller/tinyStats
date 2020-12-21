package org.tinyStats.setReconciliation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;
import org.tinyStats.setReconciliation.impl.IBLT;

public class SetReconciliationTest {

    @Test
    public void strings() {
        IBLT map = new IBLT(100);
        for (int i = 0; i < 10; i++) {
            map.remove("hello " + i);
        }
        ArrayList<String> found = map.listAddedStrings();
        if (found == null) {
            System.out.println("missed");
            return;
        }
        Collections.sort(found);
        for (String x : found) {
            System.out.println("found " + x);
        }
    }

    @Test
    public void stringsMissing() {
        IBLT map = new IBLT(800);
        for (int i = 0; i < 1000000; i++) {
            map.add("hello " + i);
        }
        ArrayList<String> notRemoved = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            if (Math.random() > 0.0001) {
                map.remove("hello " + i);
            } else {
                System.out.println("not removed " + i);
                notRemoved.add("hello " + i);
            }
        }
        ArrayList<String> found = map.listAddedStrings();
        if (found == null) {
            System.out.println("missed");
            return;
        }
        Collections.sort(found);
        for (String x : found) {
            System.out.println("found " + x);
        }
    }

    @Test
    public void numbers() {
        IBLT map = new IBLT(200);
        for (int i = 0; i < 1000000; i++) {
            map.add(i, 0);
        }
        ArrayList<Long> notRemoved = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            if (Math.random() > 0.0001) {
                map.remove(i, 0);
            } else {
                System.out.println("not removed " + i);
                notRemoved.add((long) i);
            }
        }
        HashMap<Long, Long> result = new HashMap<>();
        HashMap<Long, Long> removed = new HashMap<>();
        if (!map.list(result, removed)) {
            System.out.println("missed");
            return;
        }
        ArrayList<Long> found = new ArrayList<>(result.keySet());
        Collections.sort(found);
        for (long x : found) {
            System.out.println("found key " + x);
        }
    }

}
