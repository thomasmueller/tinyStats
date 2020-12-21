package org.tinyStats.approxCount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.tinyStats.util.Hash;

public class ApproxCountTest {

    public static void main(String... args) {
        int maxSize = 1 << 20;
        for (ApproxCountType type : ApproxCountType.values()) {
            double stdDev = test(type, maxSize, true);
            System.out.println("type " + type + " stdDev " + stdDev);
        }
    }

    @Test
    public void test() {
        assertTrue(test(ApproxCountType.EXACT, 1 << 15, false) <= 0.0);
        assertTrue(test(ApproxCountType.APPROX_4, 1 << 15, false) <= 150.0);
        assertTrue(test(ApproxCountType.APPROX_8, 1 << 15, false) <= 40.0);
        assertTrue(test(ApproxCountType.APPROX_16, 1 << 15, false) <= 35.0);
        // assertTrue(test(ApproxCountType.APPROX_LINEAR_1024, 1 << 15, false) <= 2000.0);
    }

    @Test
    public void addRemove() {
        int maxSize = 1_000_000;
        testAddRemove(ApproxCountType.EXACT, maxSize);
        testAddRemove(ApproxCountType.APPROX_LINEAR_1024, maxSize);

        for (ApproxCountType t : new ApproxCountType[] { ApproxCountType.APPROX_4, ApproxCountType.APPROX_8,
                ApproxCountType.APPROX_16, }) {
            ApproxCount a = t.construct();
            assertFalse(a.supportsRemove());
            a.remove(0);
            assertEquals(0, a.estimate());
        }
    }

    static void testAddRemove(ApproxCountType type, int maxSize) {
        ApproxCount est = type.construct();
        assertTrue(est.supportsRemove());
        for (int i = 0; i < 1_000_000; i++) {
            est.add(Hash.hash64(i, 42));
        }
        long e = est.estimate();
        assertTrue(e > 900_000);
        for (int i = 0; i < 1_000_000; i++) {
            est.remove(Hash.hash64(i, 42));
        }
        assertEquals(0L, est.estimate());
    }

    static double test(ApproxCountType type, int maxSize, boolean debug) {
        int runs = 1000;
        int[] sizes = new int[100];
        int[] histo = new int[100];
        double[] sumSquareErrors = new double[100];
        double[] sums = new double[100];
        long[] min = new long[100];
        Arrays.fill(min, Long.MAX_VALUE);
        long[] max = new long[100];
        for (int repeat = 0; repeat < runs; repeat++) {
            ApproxCount est = type.construct();
            assertEquals(0, est.estimate());
            int next = 1;
            for (int i = 0, j = 0; i <= maxSize; i++) {
                if (i == 10000) {
                    int e = (int) Math.round(est.estimate() / 1000.);
                    histo[e]++;
                }
                if (i == next) {
                    long e = est.estimate();
                    min[j] = Math.min(min[j], e);
                    max[j] = Math.max(max[j], e);
                    sums[j] += e;
                    long diff = e - i;
                    sizes[j] = i;
                    sumSquareErrors[j++] += diff * diff;
                    next += next;
                }
                long hash = Hash.hash64(i + ((long) repeat << 32));
                est.add(hash);
            }
        }
        double sumSquareP = 0;
        int j = 0;
        for (;; j++) {
            int size = sizes[j];
            if (size == 0) {
                break;
            }
            double sumSquareError = sumSquareErrors[j];
            double stdDev = Math.sqrt(sumSquareError / runs);
            double relStdDevP = stdDev / size * 100;
            if (debug) {
                System.out.println("type " + type + " count " + size + " stdDevP " + relStdDevP + " avg "
                        + sums[j] / runs + " range " + min[j] + ".." + max[j]);
            }
            sumSquareP += relStdDevP * relStdDevP;
            // System.out.println("histo10000 " + Arrays.toString(histo));
        }
        // double stdDev = Math.sqrt(sumSquareError / samples);
        double stdDev = Math.sqrt(sumSquareP / j);
        return stdDev;
    }

}
