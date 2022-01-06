package com.dipasquale.common.bit.int2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class NBitManipulatorSupportTest {
    private static final NBitManipulatorSupport TEST = new NBitManipulatorSupport(2);

    @Test
    public void TEST_1() {
        Assertions.assertEquals(32, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertFalse(TEST.isWithinBounds(-4L));
        Assertions.assertFalse(TEST.isWithinBounds(-3L));
        Assertions.assertFalse(TEST.isWithinBounds(-2L));
        Assertions.assertFalse(TEST.isWithinBounds(-1L));
        Assertions.assertTrue(TEST.isWithinBounds(0L));
        Assertions.assertTrue(TEST.isWithinBounds(1L));
        Assertions.assertTrue(TEST.isWithinBounds(2L));
        Assertions.assertTrue(TEST.isWithinBounds(3L));
        Assertions.assertFalse(TEST.isWithinBounds(4L));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(0L, TEST.extract(0L, 0L));
        Assertions.assertEquals(0L, TEST.extract(0L, 1L));
        Assertions.assertEquals(0L, TEST.extract(0L, 2L));
        Assertions.assertEquals(0L, TEST.extract(0L, 3L));
        Assertions.assertEquals(0L, TEST.extract(0L, 4L));
        Assertions.assertEquals(3L, TEST.extract(135L, 0L));
        Assertions.assertEquals(1L, TEST.extract(135L, 1L));
        Assertions.assertEquals(0L, TEST.extract(135L, 2L));
        Assertions.assertEquals(2L, TEST.extract(135L, 3L));
        Assertions.assertEquals(0L, TEST.extract(135L, 4L));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(0L, TEST.merge(0L, 0L, 0L));  // 00
        Assertions.assertEquals(0L, TEST.merge(0L, 1L, 0L));  // 00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 2L, 0L));  // 00_00_00
        Assertions.assertEquals(1L, TEST.merge(0L, 0L, 1L));  // 01
        Assertions.assertEquals(4L, TEST.merge(0L, 1L, 1L));  // 01_00
        Assertions.assertEquals(16L, TEST.merge(0L, 2L, 1L)); // 01_00_00
        Assertions.assertEquals(2L, TEST.merge(0L, 0L, 2L));  // 10
        Assertions.assertEquals(8L, TEST.merge(0L, 1L, 2L));  // 10_00
        Assertions.assertEquals(32L, TEST.merge(0L, 2L, 2L)); // 10_00_00
        Assertions.assertEquals(3L, TEST.merge(0L, 0L, 3L));  // 11
        Assertions.assertEquals(12L, TEST.merge(0L, 1L, 3L)); // 11_00
        Assertions.assertEquals(48L, TEST.merge(0L, 2L, 3L)); // 11_00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 0L, 4L));  // 00
        Assertions.assertEquals(0L, TEST.merge(0L, 1L, 4L));  // 00_00
        Assertions.assertEquals(0L, TEST.merge(0L, 2L, 4L));  // 00_00_00
    }
}