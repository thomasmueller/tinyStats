package org.tinyStats.histogram;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;
import org.tinyStats.histogram.impl.HistogramUtils;

public class HistogramUtilsTest {
    @Test
    public void lengthToBucket() {
        int bucketCount = 11;
        for (long x = 0; x < 2; x++) {
            assertEquals("x=" + x, 0, HistogramUtils.lengthTo11Buckets(x));
        }
        long min = 2, next = 16, step = 1;
        for (int bucket = 1; bucket < bucketCount; bucket++) {
            System.out.println(min + ".." + (next - 1) + " -> " + bucket);
            for (long x = min; x < next; x += step) {
                assertEquals("x=" + x, bucket, HistogramUtils.lengthTo11Buckets(x));
            }
            assertEquals("x=" + (next - 1), bucket, HistogramUtils.lengthTo11Buckets(next - 1));
            min *= 8;
            next *= 8;
            step *= 8;
        }
        Random r = new Random(42);
        for (int i = 0; i < 10000; i++) {
            long x = r.nextLong();
            if (x >= 0 && x <= 268435455) {
                i--;
                continue;
            }
            assertEquals("x=" + x, 10, HistogramUtils.lengthTo11Buckets(x));
        }
    }
}
