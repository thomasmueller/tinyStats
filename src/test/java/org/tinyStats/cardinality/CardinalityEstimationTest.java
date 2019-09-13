package org.tinyStats.cardinality;

import org.junit.Test;
import org.tinyStats.util.Hash;

public class CardinalityEstimationTest {

    static final boolean FIND_FACTOR = false;
    public static double FACTOR = 4;
    public static double STEP;

    public static void main(String... args) {
        boolean debug = true;
        int testCount = 1000;
        if (!FIND_FACTOR) {
            for (CardinalityEstimatorType type : CardinalityEstimatorType.values()) {
                double sum = 0;
                for (long size = 1; size <= 20; size++) {
                    sum += test(type, size, testCount, debug);
                }
                for (long size = 22; size <= 300; size += size / 5) {
                    sum += test(type, size, testCount, debug);
                }
                for (long size = 400; size <= Integer.MAX_VALUE; size *= 2) {
                    sum += test(type, size, testCount, debug);
                    // if (size > 3000) break;
                }
                System.out.println(type + " sum: " + sum);
            }
        }

        if (FIND_FACTOR) {
            for (CardinalityEstimatorType type : CardinalityEstimatorType.values()) {
                double bestFactor = FACTOR;
                for (double step = 1; step >= 0.0001; step /= 2) {
                    STEP = step;
                    double testFactor = bestFactor;
                    double best = Double.POSITIVE_INFINITY;
                    for (int i = -3; i <= 3; i++) {
                        double sum = 0;
                        FACTOR = testFactor + i * step;
                        System.out.println("   test " + FACTOR);
                        for (long size = 1; size <= 20; size++) {
                            sum += test(type, size, testCount, false);
                        }
                        for (long size = 22; size <= 300; size += size / 5) {
                            sum += test(type, size, testCount, false);
                        }
                        for (long size = 400; size <= Integer.MAX_VALUE; size *= 2) {
                            sum += test(type, size, testCount, false);
                            if (size > 10000) break;
                        }
                        if (sum < best) {
                            System.out.println("### factor: " + FACTOR + " i " + i + " sum " + sum + " step " + STEP);
                            best = sum;
                            bestFactor = FACTOR;
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test() {

    }

    private static double test(CardinalityEstimatorType type, long size, int testCount, boolean debug) {
        long x = 0;
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;

        if (size >= 333000) {
            if (true) {
                return 0;
            }
            // testCount = 1;
        }
        long ns = System.nanoTime();
        double sumSquareError = 0;
        double sum = 0;
        double sumFirst = 0;
        int repeat = 10;
        int runs = 2;
        for (int test = 0; test < testCount; test++) {
            CardinalityEstimator est = type.construct();
            long baseX = x;
            for (int i = 0; i < size; i++) {
                est.add(Hash.hash64(x));
                x++;
            }
            long e = est.estimate();
            sum += e;
            min = Math.min(min, e);
            max = Math.max(max, e);
            long error = e - size;
            sumSquareError += error * error;
            sumFirst += e;
            for (int add = 0; add < repeat; add++) {
                long x2 = baseX;
                for (int i = 0; i < size; i++) {
                    est.add(Hash.hash64(x2));
                    x2++;
                }
            }
            e = est.estimate();
            sum += e;
            min = Math.min(min, e);
            max = Math.max(max, e);
            error = e - size;
            sumSquareError += error * error;
        }
        ns = System.nanoTime() - ns;
        long nsPerItem = ns / testCount / runs / (1 + repeat) / size;
        double stdDev = Math.sqrt(sumSquareError / testCount / runs);
        double relStdDevP = stdDev / size * 100;
        int biasFirstP = (int) (100 * (sumFirst / testCount / size) - 100);
        int biasP = (int) (100 * (sum / testCount / runs / size) - 100);
        if (debug) {
            System.out.println("size " + size + " relStdDev% " + (int) relStdDevP +
                    " range " + min + ".." + max +
                    " testCount " + testCount +
                    " biasFirst% " + biasFirstP +
                    " bias% " + biasP +
                    " avg " + (sum / testCount / runs) +
                    " time " + nsPerItem + " type " + type);
        }
        // we try to reduce the relStdDevP, make sure there are no large values
        // (trying to reduce sumSquareError directly
        // would mean we care more about larger sets, but we don't)
        return relStdDevP * relStdDevP * relStdDevP * relStdDevP * relStdDevP;
    }

}
