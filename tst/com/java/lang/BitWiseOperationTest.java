package com.java.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BitWiseOperationTest {
    @Test
    public void TEST_1() {
        Assertions.assertEquals(Long.MIN_VALUE, 1L << 63);
        Assertions.assertEquals(Long.MAX_VALUE, ~Long.MIN_VALUE);
        Assertions.assertEquals(-1L, Long.MIN_VALUE | Long.MAX_VALUE);
        Assertions.assertEquals(1L, Long.MAX_VALUE >> 62);
        Assertions.assertEquals(0L, Long.MAX_VALUE >> 63);
        Assertions.assertEquals(1L << 62, ((Long.MAX_VALUE >> 62) << 62));
        Assertions.assertEquals(255L, Long.MAX_VALUE - ((Long.MAX_VALUE >> 8) << 8));
        Assertions.assertEquals(Integer.MAX_VALUE, (int) (Long.MAX_VALUE >> 32));
        Assertions.assertEquals(Integer.MAX_VALUE, (int) ~(1L << 31));
        Assertions.assertEquals(Integer.MAX_VALUE, Long.MAX_VALUE - ((Long.MAX_VALUE >> 31) << 31));
        Assertions.assertEquals(2305843009213693952L, Long.MIN_VALUE >>> (2 + (int) (~Long.MIN_VALUE % 7)));
        Assertions.assertEquals(-9223372036854775808L, 1L << Integer.MAX_VALUE);
        Assertions.assertEquals(-9223372036854775808L, 1L << (Integer.MAX_VALUE / 4));
    }
}
