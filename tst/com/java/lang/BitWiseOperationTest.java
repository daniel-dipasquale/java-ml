package com.java.lang;

import org.junit.Assert;
import org.junit.Test;

public final class BitWiseOperationTest {
    @Test
    public void TEST_1() {
        Assert.assertEquals(Long.MIN_VALUE, 1L << 63);
        Assert.assertEquals(Long.MAX_VALUE, ~Long.MIN_VALUE);
        Assert.assertEquals(1L, Long.MAX_VALUE >> 62);
        Assert.assertEquals(0L, Long.MAX_VALUE >> 63);
        Assert.assertEquals(1L << 62, ((Long.MAX_VALUE >> 62) << 62));
        Assert.assertEquals(255L, Long.MAX_VALUE - ((Long.MAX_VALUE >> 8) << 8));
        Assert.assertEquals(Integer.MAX_VALUE, Long.MAX_VALUE - ((Long.MAX_VALUE >> 31) << 31));
        Assert.assertEquals(2305843009213693952L, Long.MIN_VALUE >>> (2 + (int) (~Long.MIN_VALUE % 7)));
        Assert.assertEquals(-9223372036854775808L, 1L << Integer.MAX_VALUE);
        Assert.assertEquals(-9223372036854775808L, 1L << (Integer.MAX_VALUE / 4));
    }
}
