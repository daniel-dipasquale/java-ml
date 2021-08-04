package com.dipasquale.common.bit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DefaultBitManipulatorSupportTest {
    private static final DefaultBitManipulatorSupport TEST = new DefaultBitManipulatorSupport(2);

    @Test
    public void TEST_1() {
        Assertions.assertEquals(32L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertFalse(TEST.isOutOfBounds(3));
        Assertions.assertFalse(TEST.isOutOfBounds(0));
        Assertions.assertTrue(TEST.isOutOfBounds(-3));
        Assertions.assertTrue(TEST.isOutOfBounds(4));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(0L, TEST.extract(0L, 0));
        Assertions.assertEquals(0L, TEST.extract(0L, 1));
        Assertions.assertEquals(0L, TEST.extract(0L, 2));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(3L, TEST.merge(0L, 0, 3L));
        Assertions.assertEquals(15L, TEST.merge(3L, 1, 3L));
        Assertions.assertEquals(63L, TEST.merge(15L, 2, 3L));
        Assertions.assertEquals(59L, TEST.merge(63L, 1, 2L));
        Assertions.assertEquals(59L, TEST.merge(63L, 1, 2L));
    }

    @Test
    public void TEST_5() {
        Assertions.assertEquals(-2L, TEST.merge(-1L, 0, 2L));
        Assertions.assertEquals(-6L, TEST.merge(-2L, 1, 2L));
        Assertions.assertEquals(-22L, TEST.merge(-6L, 2, 2L));
    }

    @Test
    public void TEST_6() {
        long raw = 0L;

        raw = TEST.merge(raw, 0, 2L);
        Assertions.assertEquals(2L, raw);
        raw = TEST.merge(raw, 1, 3L);
        Assertions.assertEquals(14L, raw);
        raw = TEST.merge(raw, 2, 1L);
        Assertions.assertEquals(30L, raw);
        raw = TEST.merge(raw, 3, 0L);
        Assertions.assertEquals(30L, raw);
        raw = TEST.merge(raw, 4, 3L);
        Assertions.assertEquals(798L, raw);
        raw = TEST.merge(raw, 5, 2L);
        Assertions.assertEquals(2_846L, raw);
        raw = TEST.merge(raw, 6, 1L);
        Assertions.assertEquals(6_942L, raw);
        raw = TEST.merge(raw, 7, 0L);
        Assertions.assertEquals(6_942L, raw);
        raw = TEST.merge(raw, 31, 3L);
        Assertions.assertEquals(-4_611_686_018_427_380_962L, raw);
        raw = TEST.merge(raw, 30, 2L);
        Assertions.assertEquals(-2_305_843_009_213_687_010L, raw);
        raw = TEST.merge(raw, 29, 1L);
        Assertions.assertEquals(-2_017_612_633_061_975_266L, raw);
        raw = TEST.merge(raw, 28, 0L);
        Assertions.assertEquals(-2_017_612_633_061_975_266L, raw);
        Assertions.assertEquals(2L, TEST.extract(raw, 0));
        Assertions.assertEquals(3L, TEST.extract(raw, 1));
        Assertions.assertEquals(1L, TEST.extract(raw, 2));
        Assertions.assertEquals(0L, TEST.extract(raw, 3));
        Assertions.assertEquals(3L, TEST.extract(raw, 4));
        Assertions.assertEquals(2L, TEST.extract(raw, 5));
        Assertions.assertEquals(1L, TEST.extract(raw, 6));
        Assertions.assertEquals(0L, TEST.extract(raw, 7));
        Assertions.assertEquals(3L, TEST.extract(raw, 31));
        Assertions.assertEquals(2L, TEST.extract(raw, 30));
        Assertions.assertEquals(1L, TEST.extract(raw, 29));
        Assertions.assertEquals(0L, TEST.extract(raw, 28));
        Assertions.assertEquals(0L, TEST.extract(raw, 27));
    }
}

/*
    @Test
    public void TEST_7() {
        try {
            Assertions.assertEquals(0L, TEST.extract(0L, -1));
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("offset '-1' cannot be less than zero")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_8() {
        try {
            Assertions.assertEquals(0L, TEST.extract(0L, 32));
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("shifts '64' cannot be greater than '63', additional information: shifts is a multiplication between offset '32' and bits '2'")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_9() {
        try {
            Assertions.assertEquals(0L, TEST.merge(0L, -1, 1L));
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("offset '-1' cannot be less than zero")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_10() {
        try {
            Assertions.assertEquals(0L, TEST.merge(0L, 32, 1L));
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("shifts '64' cannot be greater than '63', additional information: shifts is a multiplication between offset '32' and bits '2'")
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_11() {
        try {
            Assertions.assertEquals(0L, TEST.merge(0L, 0, -1L));
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalArgumentException.class)
                    .message("value '-1' cannot differ from '3', additional information: value cannot go beyond the allowed maximum given the bits '2' being stored")
                    .build(), ThrowableAsserter.create(e));
        }
    }
 */
