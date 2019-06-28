package org.tinyStats.countSketch.impl;

import java.util.Arrays;

import org.tinyStats.countSketch.CountSketch;

public class PartList implements CountSketch {
        byte[] data = new byte[8];
        long count;

        @Override
        public void add(long hash) {
            int zeros = Long.numberOfTrailingZeros(count++);
            for (int i = 7; i >= 0; i--) {
                if ((data[i] & 0xff) == (hash & 0xff)) {
                    if (i < 7 && zeros > i) {
                        data[i] = data[i + 1];
                        data[i + 1] = (byte) (hash & 0xff);
                    }
                    break;
                }
            }
            data[0] = (byte) hash;
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
        public long estimate(long hash) {
            for (int i = 7; i >= 0; i--) {
                if ((data[i] & 0xff) == (hash & 0xff)) {
                    return i;
                }
            }
            return -1;
        }
    }