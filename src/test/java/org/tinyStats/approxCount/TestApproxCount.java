package org.tinyStats.approxCount;

import java.util.Arrays;

import org.tinyStats.util.Hash;

public class TestApproxCount {

    public static void main(String... args) {
        test();
    }

    private static void test() {
        int maxSize = 1 << 20;
        for (ApproxCountType type : ApproxCountType.values()) {
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
            for(;; j++) {
                int size = sizes[j];
                if (size == 0) {
                    break;
                }
                double sumSquareError = sumSquareErrors[j];
                double stdDev = Math.sqrt(sumSquareError / runs);
                double relStdDevP = stdDev / size * 100;
                System.out.println("type " + type + " count " + size + " stdDevP " + relStdDevP + 
                        " avg "+ sums[j] / runs + " range " + min[j] + ".." + max[j]);
                sumSquareP += relStdDevP * relStdDevP;
            }
            System.out.println("histo10000 " + Arrays.toString(histo));
            double stdDev = Math.sqrt(sumSquareP / j);
            System.out.println("type " + type + " stdDev " + stdDev);
//            double stdDev = Math.sqrt(sumSquareError / samples);
//            System.out.println("type " + type + " stdDev " + stdDev);
        }
    }
    
}
