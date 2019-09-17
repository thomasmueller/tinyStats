package org.tinyStats.histogram;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.tinyStats.histogram.impl.ExactHistogram;

public class HistogramTest {

    public static void main(String... args) {
        int bucketCount = 11;
        for (HistogramType type : HistogramType.values()) {
            System.out.println("type: " + type);
            test(type, bucketCount, true);
        }
    }

    @Test
    public void test() {
        int bucketCount = 11;
        for (HistogramType type : HistogramType.values()) {
            testPreserveOnePercent(type, bucketCount);
        }
        double result = test(HistogramType.APPROX_11, bucketCount, false);
        assertTrue(result < 25);
    }

    private static double test(HistogramType type, int bucketCount, boolean debug) {
        Random r = new Random(42);
        if (type == HistogramType.EXACT_11) {
            return 0;
        }
        double totalSquareError = 0;
        int testCount = 100;
        for (int test = 0; test < testCount; test++) {
            int size = r.nextInt(1000000);
            for (int sort = 0; sort <= 2; sort++) {
                int[] data = randomBucketData(size, r, bucketCount);
                if (sort > 0) {
                    Arrays.sort(data);
                    if (sort > 1) {
                        reverse(data);
                    }
                }
                ExactHistogram exact = new ExactHistogram(bucketCount);
                Histogram est = type.construct();
                for (int i = 0; i < size; i++) {
                    est.add(data[i]);
                    exact.add(data[i]);
                }
                int[] exactHisto = exact.getHistogram();
                int[] approxHisto = est.getHistogram();
                // System.out.println("exact histo: " + Arrays.toString(exactHisto));
                // System.out.println("approx histo: " + Arrays.toString(approxHisto));
                double sumSquareError = 0;
                for (int i = 0; i < exactHisto.length; i++) {
                    int diff = exactHisto[i] - (i >= approxHisto.length ? 0 : approxHisto[i]);
                    sumSquareError += diff * diff;
                }
                totalSquareError += sumSquareError;
            }
        }
        double result = Math.sqrt(totalSquareError / testCount);
        if (debug) {
            System.out.println("square error=" + result);
        }
        return result;
    }

    private static void testPreserveOnePercent(HistogramType type, int bucketCount) {
        for (int bucketWithOnePerent = 0; bucketWithOnePerent < bucketCount; bucketWithOnePerent++) {
            Histogram est = type.construct();
            est.add(bucketWithOnePerent);
            int otherBucket = bucketWithOnePerent == 0 ? 1 : 0;
            for (int i = 0; i < 1000000; i++) {
                est.add(otherBucket);
            }
            int[] histo = est.getHistogram();
            if (histo[bucketWithOnePerent] < 1) {
                throw new AssertionError();
            }
            if (histo[otherBucket] < 30) {
                throw new AssertionError();
            }
            for (int i = 0; i < bucketCount; i++) {
                if (i != bucketWithOnePerent && i != otherBucket) {
                    if (histo[i] != 0) {
                        throw new AssertionError();
                    }
                }
            }
        }
    }

    private static void reverse(int[] data) {
        for (int i = 0; i < data.length / 2; i++) {
            int temp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = temp;
        }
    }

    static int[] randomBucketData(int size, Random r, int bucketCount) {
        int percentTotal = 100;
        int[] percents = new int[bucketCount];
        int[] data = new int[size];
        int j = 0;
        for (int i = 0; i < bucketCount; i++) {
            if (i == 11) {
                while (j < size) {
                    data[j++] = i;
                }
            } else {
                int percent = r.nextInt(percentTotal);
                percentTotal -= percent;
                percents[i] = percent;
                for (int k = size * percent / 100; j < size && k > 0; k--) {
                    data[j++] = i;
                }
            }
        }
        return data;
    }

}
