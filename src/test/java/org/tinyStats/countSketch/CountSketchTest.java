package org.tinyStats.countSketch;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.countSketch.impl.AMSSketch;
import org.tinyStats.countSketch.impl.CountMinMeanSketch;
import org.tinyStats.countSketch.impl.CountMinSketchPercent;
import org.tinyStats.util.Hash;

public class CountSketchTest {

    public static void main(String... args) {
        int size = 1_000_000;
        for (CountSketchType type : CountSketchType.values()) {
            System.out.println("type: " + type);
            test(type, size, true);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSizeAMSSketch() {
        new AMSSketch(3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSizeCountMinSketch() {
        new CountMinSketchPercent(5, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSizeCountMinMeanSketch() {
        new CountMinMeanSketch(5, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsCountMinSketch() {
        new CountMinSketchPercent(15, 128);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgsCountMinMeanSketch() {
        new CountMinMeanSketch(15, 128);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evenHashCountMinMeanSketch() {
        new CountMinMeanSketch(4, 4);
    }

    @Test
    public void test() {
        int size = 100_000;
        CountSketchError result;

      result = test(CountSketchType.COUNT_MIN_SKETCH_PERCENT_5_16, size, false);
      assertTrue(result.stdDevRepeatRate < 3);
      assertTrue(result.stdDevEntryEstimation < 1);

        result = test(CountSketchType.COUNT_MEAN_MIN_SKETCH_5_16, size, false);
        assertTrue(result.stdDevEntryEstimation < 1);

        result = test(CountSketchType.MAJORITY_64, size, false);
        assertTrue(result.stdDevEntryEstimation < 15);

        result = test(CountSketchType.FREQUENT_2_64, size, false);
        assertTrue(result.stdDevEntryEstimation < 20);

        result = test(CountSketchType.AMS_SKETCH_8_8, size, false);
        assertTrue(result.stdDevRepeatRate < 2);
        assertTrue(result.stdDevEntryEstimation < 2);

        result = test(CountSketchType.FREQUENT_ITEM_DETECT_64, size, false);
        assertTrue(result.stdDevRepeatRate < 15);
        assertTrue(result.stdDevEntryEstimation < 20);
    }

    private static CountSketchError test(CountSketchType type, int size, boolean debug) {
        Random r = new Random(42);
        double sumSquareErrorRepeatRate = 0;
        double sumSquareErrorEntry = 0;
        int countRepeatRate = 0;
        int countEntry = 0;
        for (double skew = 2; skew < 2000; skew *= 2) {
            for (int repeat = 1; repeat <= 2; repeat++) {
                for (int sort = 0; sort <= 1; sort++) {
                    long[] data = randomData(size, skew, r, repeat);
                    long x = r.nextLong();
                    if (sort > 0) {
                        Arrays.sort(data);
                        if (sort > 1) {
                            reverse(data);
                        }
                    }
                    CountSketch est = type.construct();
                    for (int i = 0; i < size; i++) {
                        est.add(Hash.hash64(x + data[i]));
                    }
                    int[] counts = getCounts(data);
                    int expectedRepeatRate = 0;
                    for (int i = 0; i < 10; i++) {
                        expectedRepeatRate += 100 * counts[i] / size / Math.pow(2, i);
                    }
                    double er = est.estimateRepeatRate();
                    if (debug) {
                        System.out.println("skew " + skew + " repeat " + repeat + " sort " + sort + "; count(0):  "
                                + counts[0] + " est repeat " + er + " expected repeat "
                                + expectedRepeatRate);
                    }
                    double errRepeat = er - expectedRepeatRate;
                    sumSquareErrorRepeatRate += errRepeat * errRepeat;
                    countRepeatRate++;
                    for (int i = 0; i < 10; i++) {
                        long e = est.estimate(Hash.hash64(x + i));
                        long expectedPercent = (int) (100. * counts[i] / size);
                        long estPercent;
                        if (est.estimatePercent()) {
                            estPercent = e;
                        } else {
                            estPercent = (int) (100. * e / size);
                        }
                        if (debug) {
                            System.out.println("  " + i + " estimated " + e + " = " + estPercent + "%; real " + counts[i] + " = "
                                    + expectedPercent + "%");
                        }
                        double err = estPercent - expectedPercent;
                        sumSquareErrorEntry += err * err;
                        countEntry++;
                    }
                }
            }
        }
        CountSketchError result = new CountSketchError();
        result.stdDevRepeatRate =Math.sqrt(sumSquareErrorRepeatRate / countRepeatRate);
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

    static int[] getCounts(long[] data) {
        int[] counts = new int[10];
        for (int i = 0; i < 10; i++) {
            int count = 0;
            for (long d : data) {
                if (d == i) {
                    count++;
                }
            }
            counts[i] = count;
        }
        return counts;
    }

    static long[] randomData(int size, double skew, Random r, int repeat) {
        long[] data = new long[size];
        for (int i = 0; i < size; i++) {
            long m = (long) (size * Math.pow(r.nextDouble(), skew));
            if (repeat > 1) {
                m = (m / repeat * repeat) + (r.nextInt(repeat));
            }
            data[i] = m;
        }
        return data;
    }

    static class CountSketchError {
        double stdDevRepeatRate;
        double stdDevEntryEstimation;
        @Override
        public String toString() {
            return "repeat " + stdDevRepeatRate + " entry " + stdDevEntryEstimation;
        }
    }

}
