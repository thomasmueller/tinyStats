package org.tinyStats.countDistinctSketch;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;
import org.tinyStats.util.Hash;

public class CountDistinctSketchTest {

    public static void main(String... args) {
        int size = 1_000_000;
        for (CountDistinctSketchType type : CountDistinctSketchType.values()) {
            System.out.println("type: " + type);
            test(type, size, true);
        }
    }

    @Test
    public void test() {
        int size = 100_000;
        CountDistinctSketchError result;

        result = test(CountDistinctSketchType.HYPER_LOG_LOG_64_SKETCH_4_16, size, false);
        // TODO
        assertTrue(result.stdDevEntryEstimation < 1_000_000);

    }

    private static CountDistinctSketchError test(CountDistinctSketchType type, int size, boolean debug) {
        Random r = new Random(42);
        double sumSquareErrorRepeatRate = 0;
        double sumSquareErrorEntry = 0;
        int countRepeatRate = 0;
        int countEntry = 0;
        for (double skew = 2; skew < 2000; skew *= 2) {
            for (int repeat = 1; repeat <= 2; repeat++) {
                for (int sort = 0; sort <= 1; sort++) {
                    long[] data = randomKeyValueData(size, skew, r, repeat);
                    long x = r.nextLong();
                    if (sort > 0) {
                        Arrays.sort(data);
                        if (sort > 1) {
                            reverse(data);
                        }
                    }
                    CountDistinctSketch est = type.construct();
                    for (int i = 0; i < size; i++) {
                        long key = Hash.hash64(x + data[i * 2]);
                        long value = Hash.hash64(x + data[i * 2 + 1]);
                        est.add(key, value);
                    }
                    int[] countDistinct = getCountDistinct(data);
                    int expectedRepeatRate = 0;
                    for (int i = 0; i < 10; i++) {
                        expectedRepeatRate += 100 * countDistinct[i] / size / Math.pow(2, i);
                    }
                    double er = est.estimateRepeatRate();
                    if (debug) {
                        System.out.println("skew " + skew + " repeat " + repeat + " sort " + sort + "; count(0):  "
                                + countDistinct[0] + " est repeat " + er + " expected repeat " + expectedRepeatRate);
                    }
                    double errRepeat = er - expectedRepeatRate;
                    sumSquareErrorRepeatRate += errRepeat * errRepeat;
                    countRepeatRate++;
                    for (int i = 0; i < 10; i++) {
                        long e = est.estimate(Hash.hash64(x + i));
                        long expected = (int) (100. * countDistinct[i] / size);
                        if (debug) {
                            System.out.println(
                                    "  " + i + " est " + e + " real " + countDistinct[i] + " " + expected + "%");
                        }
                        double err = e - expected;
                        sumSquareErrorEntry += err * err;
                        countEntry++;
                    }
                }
            }
        }
        CountDistinctSketchError result = new CountDistinctSketchError();
        result.stdDevRepeatRate = Math.sqrt(sumSquareErrorRepeatRate / countRepeatRate);
        result.stdDevEntryEstimation = Math.sqrt(sumSquareErrorEntry / countEntry);
        return result;
    }

    private static void reverse(long[] data) {
        for (int i = 0; i < data.length / 2; i++) {
            long temp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = temp;
        }
    }

    static int[] getCountDistinct(long[] data) {
        int[] countDistinct = new int[10];
        for (int i = 0; i < 10; i++) {
            HashSet<Long> values = new HashSet<>();
            for (int j = 0; j < data.length; j += 2) {
                long key = data[j];
                long value = data[j + 1];
                if (key == i) {
                    values.add(value);
                }
            }
            countDistinct[i] = values.size();
        }
        return countDistinct;
    }

    static long[] randomKeyValueData(int size, double skew, Random r, int repeat) {
        long[] data = new long[size * 2];
        for (int i = 0; i < size; i++) {
            long m = (long) (size * Math.pow(r.nextDouble(), skew));
            if (repeat > 1) {
                m = (m / repeat * repeat) + (r.nextInt(repeat));
            }
            data[i * 2] = m;
            data[i * 2 + 1] = i;
        }
        return data;
    }

    static class CountDistinctSketchError {
        double stdDevRepeatRate;
        double stdDevEntryEstimation;

        @Override
        public String toString() {
            return "repeat " + stdDevRepeatRate + " entry " + stdDevEntryEstimation;
        }
    }

}
