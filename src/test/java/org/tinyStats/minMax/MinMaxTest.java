package org.tinyStats.minMax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.tinyStats.minMax.impl.MinMaxImpl;

public class MinMaxTest {

    @Test
    public void testNull() {
        MinMaxImpl<Long> impl = new MinMaxImpl<>();
        assertNull(impl.getMin());
        assertNull(impl.getMax());
        impl.add(null);
        assertNull(impl.getMin());
        assertNull(impl.getMax());
        impl.add(0L);
        assertEquals(0L, impl.getMin().longValue());
        assertEquals(0L, impl.getMax().longValue());
        impl.add(1L);
        assertEquals(0L, impl.getMin().longValue());
        assertEquals(1L, impl.getMax().longValue());
        impl.add(-1L);
        assertEquals(-1L, impl.getMin().longValue());
        assertEquals(1L, impl.getMax().longValue());
    }
}
