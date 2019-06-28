package org.tinyStats.countSketch;

import java.util.Arrays;
import java.util.Random;

import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.util.Hash;

public class TestCountSketch {

    public static void main(String... args) {
        test();
        test();
    }

    
    private static void test() {
        int size = 1000000;
        Random r = new Random(42);
        for (CountSketchType type : CountSketchType.values()) {
            System.out.println("type: " + type);
            for (double skew = 2; skew < 2000; skew *= 2) {
                for(int repeat = 1; repeat <= 2; repeat++) {
                    for(int sort = 0; sort <= 2; sort++) {
                        long[] data = randomData(size, skew, r, repeat);
                        long x = r.nextLong();
                        for (int i = 0; i < size; i++) {
                            data[i] += x;
                        }
                        if (sort > 0) {
                            Arrays.sort(data);
                            if (sort > 1) {
                                reverse(data);
                            }
                        }
                        CountSketch est = type.construct();
                        int[] counts = getCounts(data, x);
                        for (int i = 0; i < size; i++) {
                            est.add(Hash.hash64(data[i]));
                        }
                        System.out.println("skew " + skew + " repeat " + repeat + " sort " + sort + "; count(0):  " + counts[0] + " " + est.toString());
                        for (int i = 0; i < 10; i++) {
                            long e2 = est.estimate(Hash.hash64(x + i));
                            System.out.println(
                                    "  " + i + " est " + e2 + " got " + counts[i] + " " + (100. * counts[i] / size) + "%");
                        }
                    }
                }
            }
        }
    }
    
    private static void reverse(long[] data) {
        for(int i=0; i<data.length / 2; i++) {
            long temp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = temp;
        }
    }

    static int[] getCounts(long[] data, long x) {
        int[] counts = new int[10];
        for (int i = 0; i < 10; i++) {
            int count = 0;
            for (long d : data) {
                if (d == i + x) {
                    count++;
                }
            }
            counts[i] = count;
        }
        return counts;
    }
    
    static long[] randomData(int size, double skew, Random r, int repeat) {
        long[] data = new long[size];
        for(int i=0; i<size; i++) {
            long m =  (long) (size * Math.pow(r.nextDouble(), skew));
            if (repeat > 1) {
                m = (m / repeat * repeat) + (r.nextInt(repeat));
            }
            data[i] =  m;
        }
        return data;
    }

}