package org.tinyStats.cardinality.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

public class TestEstimation {

    public static void main(String... args) {
        test();
    }

    private static void test() {
        for (long size = 1; size <= 20; size++) {
            test(size);
        }
        for (long size = 40; size <= Integer.MAX_VALUE; size *= 2) {
            test(size);
        }
    }

    private static void test(long size) {
        for (EstimatorType type : EstimatorType.values()) {
            long x = 0;
            long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
            int testCount = 1000;

            if (size >= 333000) {
                if (true)
                    break;
                testCount = 1;
            }
            long ns = System.nanoTime();
            double sumSquareError = 0;
            double sum = 0;
            double sumFirst = 0;
            int repeat = 10;
            for (int test = 0; test < testCount; test++) {
                CardinalityEstimator est = type.construct();
                long baseX = x;
                for (int i = 0; i < size; i++) {
                    est.add(hash64(x));
                    x++;
                }
                long e = est.estimate();
                sumFirst += e;
                for (int add = 0; add < repeat; add++) {
                    long x2 = baseX;
                    for (int i = 0; i < size; i++) {
                        est.add(hash64(x2));
                        x2++;
                    }
                }
                e = est.estimate();
                sum += e;
                min = Math.min(min, e);
                max = Math.max(max, e);
                long error = e - size;
                sumSquareError += error * error;
            }
            ns = System.nanoTime() - ns;
            long nsPerItem = ns / testCount / (1 + repeat) / size;
            double stdDev = Math.sqrt(sumSquareError / testCount);
            double relStdDevP = stdDev / size * 100;
            int biasFirstP = (int) (100 * (sumFirst / testCount / size) - 100);
            int biasP = (int) (100 * (sum / testCount / size) - 100);
            System.out.println("size " + size + " relStdDev% " + (int) relStdDevP + 
                    " range " + min + ".." + max + 
                    " testCount " + testCount + 
                    " biasFirst% " + biasFirstP + 
                    " bias% " + biasP + 
                    " avg " + (sum / testCount) +
                    " time " + nsPerItem + " type " + type);

        }
    }

    public static long hash64(long x) {
        return hash64(x, 100);
    }

    public static long hash64(long x, long seed) {
        x += seed;
        x = (x ^ (x >>> 33)) * 0xff51afd7ed558ccdL;
        x = (x ^ (x >>> 23)) * 0xc4ceb9fe1a85ec53L;
        x = x ^ (x >>> 33);
        return x;
    }

}
