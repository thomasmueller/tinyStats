package org.tinyStats.cardinality;

import org.tinyStats.util.Hash;

public class TestEstimation {
    
    public static void main(String... args) {
        test();
    }

    
    private static void test() {
        if (!FIND_FACTOR) {
            for (EstimatorType type : EstimatorType.values()) {
                double sum = 0;
                for (long size = 1; size <= 20; size++) {
                    sum += test(type, size);
                }
                for (long size = 22; size <= 300; size += size / 5) {
                    sum += test(type, size);
                }
                for (long size = 400; size <= Integer.MAX_VALUE; size *= 2) {
                    sum += test(type, size);
    //              if (size > 3000) break;
                }
                System.out.println("sum: " + sum);
            }
        }
        
        if (FIND_FACTOR) {
            for (EstimatorType type : EstimatorType.values()) {
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
    //                        sum = Math.max(sum, test(type, size));
                            sum += test(type, size);
                        }
                        for (long size = 22; size <= 300; size += size / 5) {
    //                        sum = Math.max(sum, test(type, size));
                            sum += test(type, size);
                        }
                        for (long size = 400; size <= Integer.MAX_VALUE; size *= 2) {
    //                        sum = Math.max(sum, test(type, size));
                            sum += test(type, size);
                            if (size > 10000) break;
                        }
                        if (sum < best) {
                            System.out.println("### factor: " + FACTOR + " i " + i + " sum " + sum + " step " + STEP);
                            best = sum;
                            bestFactor = FACTOR;
                        }
                    }
    //                break;
                }
            }
        }
        
    }
    
//    public static double FACTOR = 1.95;
//    public static double STEP = 0.01;
    static final boolean FIND_FACTOR = false;
    public static double FACTOR = 4;
    public static double STEP;

    

    private static double test(EstimatorType type, long size) {
        long x = 0;
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        int testCount = 1000;

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
        if (!FIND_FACTOR) {
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
