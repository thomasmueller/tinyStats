package org.tinyStats.cardinality.impl.int64;

import org.tinyStats.cardinality.CardinalityEstimator;

/**
 * Cardinality estimation with the HyperLogLog algorithm, using the tail cut
 * mechanism. Tail cut is described in the paper "Better with Fewer Bits -
 * Improving the Performance of Cardinality Estimation of Large Data Streams"
 * from Qingjun Xiao, You Zhou, Shigang Chen, in
 * http://cse.seu.edu.cn/PersonalPage/csqjxiao/csqjxiao_files/papers/INFOCOM17.pdf
 * 
 * Uses 20 counters of 3 bits each, and 4 bits for a base counter, which is
 * increased if all counters are larger than zero.
 * 
 * It is a little bit "order-dependent", that is, adding the same entry multiple
 * times can change the internal state. However, unlike in HyperBitBit, here the
 * effect is smaller.
 */
public class HyperLogLog3TailCut64 implements CardinalityEstimator {

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
            int i = (int) (((hash & 0xffffffffL) * 20) >>> 32);
            int shift = 4 + 3 * i;
            long old = (data >>> shift) & 0x7;
            long m = Math.min(0x7, Math.max(z,  old));
            data = (data & ~(0x7L << shift)) | (m << shift);
            // base shift
            long s = data | (data >>> 1) | (data >>> 2);
            if ((s & 0x2492492492492490L) == 0x2492492492492490L) {
                if (base < 0xf) {
                    data -= 0x2492492492492490L;
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
        for (int i = 1; i < 20; i++) {
            long n = x & 0x7;
            x >>>= 3;
            countZero += n == 0 ? 1 : 0;
            sum += 1. / (1L << (base - 1 + n));
        }
        double est;
        if (base <= 1 && countZero > 0) {
            // linear counting
            int m = 20;
            est = 0.89 * m * Math.log((double) m / countZero);            
        } else {
            est = 20 * 20 * 0.61 / sum;
        }
        return Math.max(1, (long) est);
    }
    
}
