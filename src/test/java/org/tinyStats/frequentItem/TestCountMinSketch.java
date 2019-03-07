package org.tinyStats.frequentItem;

import java.util.Arrays;
import java.util.Random;

public class TestCountMinSketch {

    public static void main(String... args) {
        test();
//        test();
    }

    
    private static void test() {
        int size = 1000000;
        Random r = new Random(42);
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
                    int[] counts = getCounts(data, x);
                    for (CountSketchType type : CountSketchType.values()) {
                        CountSketch est = type.construct();
                        for (int i = 0; i < size; i++) {
                            est.add(data[i]);
                        }
                        System.out.println("skew " + skew + " repeat " + repeat + " sort " + sort + "; count(0):  " + counts[0] + " " + est.toString());
                        for (int i = 0; i < 10; i++) {
                            long e2 = est.estimate(x + i);
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

    public static long hash64(long x, long seed) {
        x += seed;
        x = (x ^ (x >>> 33)) * 0xff51afd7ed558ccdL;
        x = (x ^ (x >>> 23)) * 0xc4ceb9fe1a85ec53L;
        x = x ^ (x >>> 33);
        return x;
    }

    static interface CountSketch {
        void add(long x);
        long estimate(long x);
    }
    
    enum CountSketchType {
//        COUNT_MIN_SKETCH2 {
//            @Override
//            public CountSketch construct() {
//                return new CountMinSketch2();
//            }
//        };
//        COUNT_MIN_SKETCH {
//            @Override
//            public CountSketch construct() {
//                return new CountMinSketch();
//            }
//        };
        PART_LIST {
            @Override
            public CountSketch construct() {
                return new PartList();
            }
        };
//        REINFORCE {
//            @Override
//            public CountSketch construct() {
//                return new Reinforce();
//            }
//        };
        public abstract CountSketch construct();

    }
    
    static class CountMinSketch implements CountSketch {
        final static long K = 2;
        long count;
        int[] data = new int[16];

        @Override
        public void add(long x) {
            long y = hash64(x, 1);
            int zeros = Long.numberOfTrailingZeros(count++);
            int min = 0x10;
            int minShift = -1;
            for(int k=0; k < K; k++) {
                int i = (int) ((y >>> (k * 4)) & 15);
                int shift = i * 4;
                int m = data[shift / 4];
//                int m = (int) (data >>> shift) & 0xf;
                if (m < min) {
                    min = m;
                    minShift = shift;
                }
            }
            if (zeros < min) {
                return;
            }
            if (min == 0xf) {
                for(int i=0; i<data.length; i++) {
                    boolean dec = true;
                    for(int k=0; k < K; k++) {
                        int ii = (int) ((y >>> (k * 4)) & 15);
                        if (ii == i) {
                            dec = false;
                            break;
                        }
                    }
                    if (dec) {
                        int zeros2 = Long.numberOfTrailingZeros(hash64(count++, 1));
                        if (zeros2 > data[i]) {
                            data[i] = Math.max(0, data[i] - 1);
                        }
                    }
                }
//                data -= 0x1111111111111111L;
                min--;
            }
            data[minShift / 4] = min+1;
        }
        
        public String toString() {
            return Arrays.toString(data);
//            return Long.toHexString(data);
        }

        @Override
        public long estimate(long x) {
            long y = hash64(x, 1);
            int min = 0xf;
//            System.out.print("  est " + x + ": ");
            for(int k=0; k < K; k++) {
                int i = (int) ((y >>> (k * 4)) & 15);
                int shift = i * 4;
                int m = data[shift / 4];
//                System.out.print(" [" + shift +"]: " + m);
//                int m = (int) (data >>> shift) & 0xf;
                min = Math.min(min, m);
            }
//            System.out.println();
            int sum = 0;
            
            for(int i=0; i < 16; i++) {
                int shift = i * 4;
                int m = data[shift / 4];
//                int m = (int) (data >>> shift) & 0xf;
                sum += m;
            }
            double avg = sum / 16.;
            return (int) (100 * Math.pow(2, min - avg));
        }
    }
    
    
    static class CountMinSketch2 implements CountSketch {
        final static long K = 3;
        final static int BITS = 2;
        final static int MAX = 3;
        final static int B2 = 5;
        final static int M2 = 31;
        long count;
//        long data;
        int[] data = new int[32];

        @Override
        public void add(long x) {
            long y = hash64(x, 1);
            int zeros = Long.numberOfTrailingZeros(count++);
            int min = 0x8;
            int minShift = -1;
            for(int k=0; k < K; k++) {
                int i = (int) ((y >>> (k * B2)) & M2);
                int m = data[i];
                if (m < min) {
                    min = m;
                    minShift = i;
                }
            }
            if (zeros < min) {
                return;
            }
            if (min == MAX) {
                for(int i=0; i<data.length; i++) {
                    boolean dec = true;
                    for(int k=0; k < K; k++) {
                        int ii = (int) ((y >>> (k * B2)) & M2);
                        if (ii == i) {
                            dec = false;
                            break;
                        }
                    }
                    if (dec) {
                        int zeros2 = Long.numberOfTrailingZeros(hash64(count++, 1));
                        if (zeros2 > data[i]) {
                            data[i] = Math.max(0, data[i] - 1);
                        }
                    }
                }
                min--;
            }
            data[minShift] = min+1;
        }
        
        public String toString() {
            return Arrays.toString(data);
        }

        @Override
        public long estimate(long x) {
            long y = hash64(x, 1);
            int min = 0xf;
            for(int k=0; k < K; k++) {
                int i = (int) ((y >>> (k * B2)) & M2);
                int m = data[i];
                min = Math.min(min, m);
            }
            int sum = 0;
            for(int i=0; i<data.length; i++) {
                int m = data[i];
                sum += m;
            }
            double avg = sum / (double) data.length;
            return (int) (100 * Math.pow(2, min - avg));
        }
    }    
    
    static class Reinforce implements CountSketch {
        short[] data = new short[4];
        long count;

        @Override
        public void add(long x) {
            long y = hash64(x, 1);
            int zeros = Long.numberOfTrailingZeros(count++);
            int minSimilarityI = -1;
            int minSimilarity = 255;
            if (zeros > 4) {
                for (int i = 0; i < 4; i++) {
                    long diff = ((data[i] & 0xffff) ^ y) & 0xffff;
                    int similarity = Long.bitCount(diff);
                    if (similarity < minSimilarity) {
                        minSimilarity = similarity;
                        minSimilarityI = i;
                    }
                }
                if (minSimilarity > 0) {
                    int d = data[minSimilarityI] & 0xffff;
                    for(int i=0; i<16; i++) {
                        int b = (int) ((i + count) & 0xf);
                        if ((((d ^ y) >>> b) & 1) == 1) {
                            d ^= 1L << b;
                            data[minSimilarityI] = (short) d;
        long diff = ((data[minSimilarityI] & 0xffff) ^ y) & 0xffff;
        int similarity = Long.bitCount(diff);
        if (similarity != minSimilarity - 1) {
            System.out.println("??");
        }
                            break;
                        }
                    }
                }
            }                
        }
        
        public String toString() {
            return Arrays.toString(data);
        }

        @Override
        public long estimate(long x) {
            long y = hash64(x, 1);
            int minSimilarity = 255;
            for (int i = 0; i < 4; i++) {
                long diff = ((data[i] & 0xffff) ^ y) & 0xffff;
                int similarity = Long.bitCount(diff);
                if (similarity < minSimilarity) {
                    minSimilarity = similarity;
                }
            }
            return minSimilarity;
        }
    }    
    
    static class PartList implements CountSketch {
        byte[] data = new byte[8];
        long count;

        @Override
        public void add(long x) {
            long y = hash64(x, 1);
            int zeros = Long.numberOfTrailingZeros(count++);
            for (int i = 7; i >= 0; i--) {
                if ((data[i] & 0xff) == (y & 0xff)) {
                    if (i < 7 && zeros > i) {
                        data[i] = data[i + 1];
                        data[i + 1] = (byte) (y & 0xff);
                    }
                    break;
                }
            }
            data[0] = (byte) y;
//            for(int m = 0; m <= zeros / 4; m++) {
//                int b = (int) count++;
//                int d = data[0] & 0xff;
//                if ((((d ^ y) >>> b) & 1) == 1) {
//                    d ^= 1L << b;
//                    data[0] = (byte) d;
//                    if ((data[0] & 0xff) == (y & 0xff)) {
//                        data[0] = data[1];
//                        data[1] = (byte) (y & 0xff);
//                    }
//                }            
//            }
        }
        
        public String toString() {
            return Arrays.toString(data);
        }

        @Override
        public long estimate(long x) {
            long y = hash64(x, 1);
            for (int i = 7; i >= 0; i--) {
                if ((data[i] & 0xff) == (y & 0xff)) {
                    return i;
                }
            }
            return -1;
        }
    }    
    
}
