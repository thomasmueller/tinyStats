package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;
import org.tinyStats.util.Hash;

public class CountMinSketch implements CountSketch {
        final static long K = 2;
        long count;
        int[] data = new int[16];

        @Override
        public void add(long hash) {
            int zeros = Long.numberOfTrailingZeros(count++);
            int min = 0x10;
            int minShift = -1;
            for(int k=0; k < K; k++) {
                int i = (int) ((hash >>> (k * 4)) & 15);
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
                        int ii = (int) ((hash >>> (k * 4)) & 15);
                        if (ii == i) {
                            dec = false;
                            break;
                        }
                    }
                    if (dec) {
                        int zeros2 = Long.numberOfTrailingZeros(Hash.hash64(count++, 1));
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
        public long estimate(long hash) {
            int min = 0xf;
//            System.out.print("  est " + x + ": ");
            for(int k=0; k < K; k++) {
                int i = (int) ((hash >>> (k * 4)) & 15);
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