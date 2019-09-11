package org.tinyStats.median;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.tinyStats.util.Hash;

public class MedianTest {

    private final static boolean DEBUG = false;

    public static void main(String... args) {
        for (MedianType type : MedianType.values()) {
            System.out.println(type + " ==============");
            for (int b = 3; b <= 11; b += 2) {
                double error = test(type, b);
                System.out.println(b + " " + error);
            }
        }
    }

    @Test
    public void test() {
        assertTrue(test(MedianType.REMEDIAN_SIMPLE, 5) <= 15.0);
        assertTrue(test(MedianType.REMEDIAN_PLUS, 5) <= 5.0);
    }

    private static double test(MedianType type, int b) {
        double sumSquareError = 0;
        int count = 0;
        for (int sort = 0; sort <= 2; sort++) {
            int maxSize = 1000000;
            int[] data = new int[maxSize];
            for (int size = 2; size < maxSize; size += size < 20 ? 1 : size / 5) {
                ApproxMedian<Integer> med = type.construct(b);
                for (int i = 0; i < size; i++) {
                    int x = sort == 0 ? i : sort == 1 ? size - i : (int) Hash.hash64(i);
                    med.add(x);
                    data[i] = x;
                    if (DEBUG) {
                        // System.out.println(" " + med);
                    }
                }
                Integer m = med.getApproxMedian();
                int got = m == null ? -1 : m;
                Arrays.sort(data, 0, size);
                int median = data[size / 2];
                int diffIndex = 0;
                int x = size / 2;
                if (median > got) {
                    while (data[x] > got) {
                        x--;
                        diffIndex++;
                    }
                } else if (median < got) {
                    while (data[x] < got) {
                        x++;
                        diffIndex++;
                    }
                }
                // int diff = 100 * (got - median) / median;
                int diff = 100 * diffIndex / size;
                if (DEBUG) {
                    System.out.println("size " + size + " b " + b + " sort " + sort + " median " + median + " got " + m
                            + " diffIndex + " + diffIndex + " diff " + (diff >= 0 ? "+" + diff : diff) + "%");
                }
                sumSquareError += diff * diff;
                count++;
                // return;
            }
            // average error (root of avg sqare error)
        }
        return Math.sqrt(sumSquareError / count);
    }
}
