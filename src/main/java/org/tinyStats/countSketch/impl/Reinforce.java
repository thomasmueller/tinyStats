package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;

public class Reinforce implements CountSketch {
    short[] data = new short[4];
    long count;

    @Override
    public void add(long hash) {
        int zeros = Long.numberOfTrailingZeros(count++);
        int minSimilarityI = -1;
        int minSimilarity = 255;
        if (zeros > 4) {
            for (int i = 0; i < 4; i++) {
                long diff = ((data[i] & 0xffff) ^ hash) & 0xffff;
                int similarity = Long.bitCount(diff);
                if (similarity < minSimilarity) {
                    minSimilarity = similarity;
                    minSimilarityI = i;
                }
            }
            if (minSimilarity > 0) {
                int d = data[minSimilarityI] & 0xffff;
                for (int i = 0; i < 16; i++) {
                    int b = (int) ((i + count) & 0xf);
                    if ((((d ^ hash) >>> b) & 1) == 1) {
                        d ^= 1L << b;
                        data[minSimilarityI] = (short) d;
                        long diff = ((data[minSimilarityI] & 0xffff) ^ hash) & 0xffff;
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
    public long estimate(long hash) {
        int minSimilarity = 255;
        for (int i = 0; i < 4; i++) {
            long diff = ((data[i] & 0xffff) ^ hash) & 0xffff;
            int similarity = Long.bitCount(diff);
            if (similarity < minSimilarity) {
                minSimilarity = similarity;
            }
        }
        return minSimilarity;
    }

    @Override
    public long estimateRepeatRate() {
        return 0;
    }

}