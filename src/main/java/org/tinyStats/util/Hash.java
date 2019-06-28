package org.tinyStats.util;

public class Hash {

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

}
