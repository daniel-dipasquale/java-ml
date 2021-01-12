package com.dipasquale.common;

import org.junit.Assert;
import org.junit.Test;

public final class BitManipulatorSupport64Test {
    private static final BitManipulatorSupport64 TEST = new BitManipulatorSupport64();

    @Test
    public void TEST_1() {
        Assert.assertEquals(1L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assert.assertFalse(TEST.isOutOfBounds(Long.MAX_VALUE));
        Assert.assertFalse(TEST.isOutOfBounds(Long.MIN_VALUE));
    }

    @Test
    public void TEST_3() {
        Assert.assertEquals(0L, TEST.extract(0L, 1L));
        Assert.assertEquals(0L, TEST.extract(0L, 2L));
        Assert.assertEquals(1L, TEST.extract(1L, 2L));
        Assert.assertEquals(1L, TEST.extract(1L, 3L));
    }

    @Test
    public void TEST_4() {
        Assert.assertEquals(2L, TEST.merge(0L, 1L, 2L));
        Assert.assertEquals(4L, TEST.merge(0L, 2L, 4L));
        Assert.assertEquals(4L, TEST.merge(1L, 2L, 4L));
        Assert.assertEquals(6L, TEST.merge(1L, 3L, 6L));
    }
}
