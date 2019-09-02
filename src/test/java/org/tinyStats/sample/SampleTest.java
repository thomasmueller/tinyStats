package org.tinyStats.sample;

import org.tinyStats.sample.impl.ReservoirSampling;
import org.tinyStats.util.Hash;

public class SampleTest {
    public static void main(String... args) {
        test();
    }

    private static void test() {
        Hash.setSeed(42);
        int testCount = 10000;
        int size = 1000;
        int sampleSize = 10;
        int[] counts = new int[size];
        for (int test = 0; test < testCount; test++) {
            ReservoirSampling<Integer> sample = new ReservoirSampling<>(sampleSize);
            for (int i = 0; i < size; i++) {
                sample.add(i);
            }
            for (Integer x : sample.list()) {
                counts[x]++;
            }
        }
        int expected = testCount * sampleSize / size;
        for (int c : counts) {
            if (c <= expected * .7 || c >= expected * 1.5) {
                throw new AssertionError("expected " + expected + " got " + c);
            }
        }
    }
}
