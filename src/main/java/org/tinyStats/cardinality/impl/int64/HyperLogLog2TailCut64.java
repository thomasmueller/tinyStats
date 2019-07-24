package org.tinyStats.cardinality.impl.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperLogLog algorithm, using the tail cut
 * mechanism. Tail cut is described in the paper "Better with Fewer Bits -
 * Improving the Performance of Cardinality Estimation of Large Data Streams"
 * from Qingjun Xiao, You Zhou, Shigang Chen, in
 * http://cse.seu.edu.cn/PersonalPage/csqjxiao/csqjxiao_files/papers/INFOCOM17.pdf
 * 
 * Uses 30 counters of 2 bits each, and 4 bits for a base counter, which is
 * increased if all counters are larger than zero.
 * 
 * It is a bit "order-dependent", that is, adding the same entry multiple times
 * can change the internal state. However, unlike in HyperBitBit, here the
 * effect is smaller.
 */
public class HyperLogLog2TailCut64 implements CardinalityEstimator {

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
            int i = (int) (((hash & 0xffffffffL) * 30) >>> 32);
            int shift = 4 + 2 * i;
            long old = (data >>> shift) & 0x3;
            long m = Math.min(0x3, Math.max(z,  old));
            data = (data & ~(0x3L << shift)) | (m << shift);
            // base shift
            long s = data | (data >>> 1);
            if ((s & 0x5555555555555550L) == 0x5555555555555550L) {
                if (base < 0xf) {
                    data -= 0x5555555555555550L;
                    data += 1;
                }
            }
        }
        return data;
    }
    
    static long estimate(long data) {
        long base = 1 + (data & 0xf);
        double sum = 0;
        long x = data >>> 4;
        int countZero = 0;
        for (int i = 1; i < 30; i++) {
            long n = x & 0x3;
            x >>>= 2;
            countZero += n == 0 ? 1 : 0;
            sum += 1. / (1L << (base - 1 + n));
        }
        double est;
        if (base <= 1 && countZero > 0) {
            // linear counting
            int m = 30;
            est = 0.9125 * m * Math.log((double) m / countZero);            
        } else {
            est = 30 * 30 * 0.785 / sum;
        }
        return Math.max(1, (long) est);
    }
    
}
