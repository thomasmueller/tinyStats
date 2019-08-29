package org.tinyStats.histogram.impl;

public class HistogramUtils {

    /**
     * The the bucket for the specified length. The following mapping is used:
     * 0..1 -> 0,
     * 2..15 -> 1,
     * 16..127 -> 2,
     * 128..1023 -> 3,
     * 1024..8191 -> 4,
     * 8192..65535 -> 5,
     * 65536..524287 -> 6,
     * 524288..4194303 -> 7,
     * 4194304..33554431 -> 8,
     * 33554432..268435455 -> 9,
     * everything else -> 10
     *
     * @param length the length
     * @return the bucket (0..10)
     */
    public static int lengthTo11Buckets(long length) {
        int logLength = Math.max(0, 63 - Long.numberOfLeadingZeros(length));
        int bucket = Math.min(10, (logLength + 2) / 3);
        return bucket;
    }

}
