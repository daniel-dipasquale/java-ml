package com.dipasquale.common.bit.int2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class Only64BitManipulatorSupportTest {
    private static final Only64BitManipulatorSupport TEST = new Only64BitManipulatorSupport();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(1L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertTrue(TEST.isWithinBounds(Long.MIN_VALUE));
        Assertions.assertTrue(TEST.isWithinBounds(0L));
        Assertions.assertTrue(TEST.isWithinBounds(Long.MAX_VALUE));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(0L, TEST.extract(0L, 0L));
        Assertions.assertEquals(0L, TEST.extract(0L, 1L));
        Assertions.assertEquals(1L, TEST.extract(1L, 0L));
        Assertions.assertEquals(1L, TEST.extract(1L, 1L));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(2L, TEST.merge(0L, 0L, 2L));
        Assertions.assertEquals(2L, TEST.merge(0L, 1L, 2L));
        Assertions.assertEquals(2L, TEST.merge(1L, 0L, 2L));
        Assertions.assertEquals(2L, TEST.merge(1L, 1L, 2L));
    }
}
