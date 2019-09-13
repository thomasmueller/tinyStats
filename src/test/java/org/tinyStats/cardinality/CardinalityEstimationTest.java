package org.tinyStats.cardinality;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tinyStats.util.Hash;

public class CardinalityEstimationTest {

    static final boolean FIND_FACTOR = false;
    public static double FACTOR = 4;
    public static double STEP;

    public static void main(String... args) {
        int testCount = 1000;
        double exponent = 5;
        if (!FIND_FACTOR) {
            for (CardinalityEstimatorType type : CardinalityEstimatorType.values()) {
                double sum = averageOverRange(type, 333_000, testCount, true, exponent);
                System.out.println(type + " avg: " + sum);
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
                        FACTOR = testFactor + i * step;
                        System.out.println("   test " + FACTOR);
                        double sum = averageOverRange(type, 10_000, testCount, false, exponent);
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
        int testCount = 50;
        for (CardinalityEstimatorType type : CardinalityEstimatorType.values()) {
            double avg = Math.sqrt(averageOverRange(type, 50_000, testCount, false, 2));
            // System.out.println(type + " avg " + avg);
            double min, max;
            switch(type) {
            case HYPER_LOG_LOG:
                min = 20;
                max = 30;
                break;
            case HYPER_BIT_BIT:
                min = 10_000;
                max = 30_000;
                break;
            case HYPER_BIT_BIT_64:
                min = 500;
                max = 700;
                break;
            case HYPER_LOG_LOG_5_BIT_64:
                min = 40;
                max = 50;
                break;
            case LINEAR_COUNTING_64:
                min = 30;
                max = 40;
                break;
            case HYPER_LOG_LOG_2_TAILCUT_64:
                min = 19;
                max = 20;
                break;
            case HYPER_LOG_LOG_3_TAILCUT_64:
                min = 22;
                max = 23;
                break;
            case HYPER_LOG_LOG_4_TAILCUT_64:
                min = 24;
                max = 25;
                break;
            case HYPER_LOG_LOG_2_LINEAR_64:
                min = 16;
                max = 17;
                break;
            case HYPER_LOG_LOG_3_LINEAR_64:
                min = 16;
                max = 17;
                break;
            default:
                min = 0;
                max = 0;
                break;
            }
            assertTrue(type + " expected " + min + ".." + max + " got " + avg, min < avg && avg < max);
        }
    }

    private static double averageOverRange(CardinalityEstimatorType type, long maxSize, int testCount, boolean debug, double exponent) {
        double sum = 0;
        int count = 0;
        for (long size = 1; size <= 20; size++) {
            sum += test(type, size, testCount, false, exponent);
            count++;
        }
        for (long size = 22; size <= 300; size += size / 5) {
            sum += test(type, size, testCount, false, exponent);
            count++;
        }
        for (long size = 400; size <= maxSize; size *= 2) {
            sum += test(type, size, testCount, false, exponent);
            count++;
        }
        return sum / count;
    }

    private static double test(CardinalityEstimatorType type, long size, int testCount, boolean debug, double exponent) {
        long x = 0;
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
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
        return Math.pow(relStdDevP, exponent);
    }

}
