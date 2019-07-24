package org.tinyStats.cardinality.impl.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperLogLog algorithm, using the tail cut
 * mechanism. Tail cut is described in the paper "Better with Fewer Bits -
 * Improving the Performance of Cardinality Estimation of Large Data Streams"
 * from Qingjun Xiao, You Zhou, Shigang Chen, in
 * http://cse.seu.edu.cn/PersonalPage/csqjxiao/csqjxiao_files/papers/INFOCOM17.pdf
 * 
 * Uses 15 counters of 4 bits each, and 4 bits for a base counter, which is
 * increased if all counters are larger than zero.
 * 
 * It is a tiny bit "order-dependent", that is, adding the same entry multiple
 * times can change the internal state. However, unlike in HyperBitBit, here the
 * effect is minimal: usually less than 1%.
 */
public class HyperLogLog4TailCut64 implements CardinalityEstimator {

    private long data;

    @Override
    public void add(long hash) {
        data = add(data, hash);
    }
    
    @Override
    public long estimate() {
        return estimate(data);
    }
    
    static long add(long data, long hash) {
        int base = (int) (data & 0xf);
        int z = Long.numberOfLeadingZeros(hash) + 1 - base;
        if (z > 0) {
            int i = 1 + (int) (((hash & 0xffffffffL) * 15) >>> 32);
            int shift = 4 * i;
            long old = (data >>> shift) & 0xf;
            long m = Math.min(0xf, Math.max(z,  old));
            data = (data & ~(0xfL << shift)) | (m << shift);
            // base shift
            long s = data | (data >>> 1);
            s = s | (s >> 2);
            if ((s & 0x1111111111111110L) == 0x1111111111111110L) {
                if (base < 0xf) {
                    data -= 0x1111111111111110L;
                    data += 1;
                }
            }
        }
        return data;
    }
    
    static long estimate(long data) {
        long base = 1 + (data & 0xf);
        double sum = 0;
        long x = data;
        int countZero = 0;
        for (int i = 1; i < 16; i++) {
            x >>>= 4;
            long n = x & 0xf;
            countZero += n == 0 ? 1 : 0;
            sum += 1. / (1L << (base - 1 + n));
        }
        double est;
        if (base <= 1 && countZero > 0) {
            // linear counting
            int m = 15;
            est = 15 * 0.95 * Math.log((double) m / countZero);            
        } else {
            est = 15 * 15 * 0.62 / sum;
        }
        return Math.max(1, (long) est);
    }

    
}
