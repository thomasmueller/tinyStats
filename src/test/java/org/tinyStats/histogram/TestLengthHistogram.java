package org.tinyStats.histogram;

import java.util.Arrays;
import java.util.Random;

import org.tinyStats.histogram.impl.ExactLengthHistogram;
import org.tinyStats.util.Hash;

public class TestLengthHistogram {

    public static void main(String... args) {
        test();
    }

    private static void test() {
        int size = 1000000;
        Random r = new Random(42);
        for (LengthHistogramType type : LengthHistogramType.values()) {
            testPreserveOnePercent(type);
            if (type == LengthHistogramType.EXACT_LENGTH_HISTOGRAM) {
                continue;
            }
            System.out.println("type: " + type);
            double totalSquareError = 0;
            for(int test = 0; test < 10; test++) {
                for (int sort = 0; sort <= 2; sort++) {
                    long[] data = randomLength(size, r);
                    if (sort > 0) {
                        Arrays.sort(data);
                        if (sort > 1) {
                            reverse(data);
                        }
                    }
                    ExactLengthHistogram exact = new ExactLengthHistogram();
                    LengthHistogram est = type.construct();
                    for (int i = 0; i < size; i++) {
                        est.add(Hash.hash64(i), data[i]);
                        exact.add(Hash.hash64(i), data[i]);
                    }
                    int[] exactHisto = exact.getHistogram();
                    int[] approxHisto = est.getHistogram();
                    System.out.println("exact histo: " + Arrays.toString(exactHisto));
                    System.out.println("approx histo: " + Arrays.toString(approxHisto));
                    double sumSquareError = 0;
                    for (int i = 0; i < exactHisto.length; i++) {
                        int diff = exactHisto[i] - (i >= approxHisto.length ? 0 : approxHisto[i]);
                        sumSquareError += diff * diff;
                    }
                    totalSquareError += sumSquareError;
                }
            }
            System.out.println("square error=" + totalSquareError);
        }
    }

    private static void testPreserveOnePercent(LengthHistogramType type) {
        for (long sizeWithOnePerent = 8, bucket = 1; sizeWithOnePerent <= 10_000_000_000L; sizeWithOnePerent *= 8, bucket++) {
            LengthHistogram est = type.construct();
            est.add(123, sizeWithOnePerent);
            for (int i = 0; i < 1000000; i++) {
                est.add(Hash.hash64(i), 1);
            }
            int[] histo = est.getHistogram();
            if (histo[(int) bucket] < 1) {
                throw new AssertionError();
            }
        }
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

    static long[] randomLength(int size, Random r) {
        int percentTotal = 100;
        int[] percents = new int[12];
        long[] data = new long[size];
        int j = 0;
        long len = 1;
        for (int i = 0; i < 12; i++) {
            if (i == 11) {
                while (j < size) {
                    data[j++] = len;
                }
            } else {
                int percent = r.nextInt(percentTotal);
                percentTotal -= percent;
                percents[i] = percent;
                for (int k = size * percent / 100; j < size && k > 0; k--) {
                    data[j++] = len;
                }
            }
            len *= 8;
        }
        return data;
    }

}
