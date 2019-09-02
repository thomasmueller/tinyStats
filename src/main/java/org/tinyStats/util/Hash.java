package org.tinyStats.util;

public class Hash {

    private static long seed;

    public static void setSeed(long s) {
        seed = s;
    }

    public static long randomLong() {
        return hash64(seed++);
    }

    public static long hash64(long x, long seed) {
        x += seed;
        x = (x ^ (x >>> 33)) * 0xff51afd7ed558ccdL;
        x = (x ^ (x >>> 23)) * 0xc4ceb9fe1a85ec53L;
        x = x ^ (x >>> 33);
        return x;
    }

    public static long hash64(long x) {
        return hash64(x, 100);
    }

    /**
     * Shrink the hash to a value 0..n. Kind of like modulo, but using
     * multiplication and shift, which are faster to compute.
     *
     * @param hash the hash
     * @param n the maximum of the result
     * @return the reduced value
     */
    public static int reduce(int hash, int n) {
        // http://lemire.me/blog/2016/06/27/a-fast-alternative-to-the-modulo-reduction/
        return (int) (((hash & 0xffffffffL) * n) >>> 32);
    }

}
