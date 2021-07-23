package com.dipasquale.common.bit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BitManipulatorSupportTest {
    private static final BitManipulatorSupport TEST = new BitManipulatorSupportMock();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(2L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertTrue(TEST.isOutOfBounds((long) Integer.MAX_VALUE + 1L));
        Assertions.assertFalse(TEST.isOutOfBounds(0L));
        Assertions.assertTrue(TEST.isOutOfBounds(-1L));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.extract(Integer.MAX_VALUE | 1L << 32, 0L));
        Assertions.assertEquals(1L, TEST.extract(Integer.MAX_VALUE | 1L << 32, 1L));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(Integer.MIN_VALUE, TEST.merge(Integer.MAX_VALUE, 0L, Integer.MIN_VALUE));
        Assertions.assertEquals(Integer.MAX_VALUE | (1L << 32), TEST.merge(Integer.MAX_VALUE, 1L, 1L));
    }

    @Test
    public void TEST_5() {
        Assertions.assertEquals(1L, TEST.setAndGet(Integer.MAX_VALUE, 0L, 1L));
        Assertions.assertEquals(2L, TEST.setAndGet(Integer.MAX_VALUE, 1L, 2L));
    }

    @Test
    public void TEST_6() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.getAndSet(Integer.MAX_VALUE, 0L, 1L));
        Assertions.assertEquals(0L, TEST.getAndSet(Integer.MAX_VALUE, 1L, 2L));
    }

    @Test
    public void TEST_7() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.accumulateAndGet(Integer.MAX_VALUE, 0L, 1L, (o, n) -> o));
        Assertions.assertEquals(1L, TEST.accumulateAndGet(Integer.MAX_VALUE, 0L, 1L, (o, n) -> n));
        Assertions.assertEquals(0L, TEST.accumulateAndGet(Integer.MAX_VALUE, 1L, 2L, (o, n) -> o));
        Assertions.assertEquals(2L, TEST.accumulateAndGet(Integer.MAX_VALUE, 1L, 2L, (o, n) -> n));
    }

    @Test
    public void TEST_8() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.getAndAccumulate(Integer.MAX_VALUE, 0L, 1L, (o, n) -> o));
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.getAndAccumulate(Integer.MAX_VALUE, 0L, 1L, (o, n) -> n));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(Integer.MAX_VALUE, 1L, 2L, (o, n) -> o));
        Assertions.assertEquals(0L, TEST.getAndAccumulate(Integer.MAX_VALUE, 1L, 2L, (o, n) -> n));
    }

    @Test
    public void TEST_9() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.addAndGet(Integer.MAX_VALUE, 0L, 0L));
        Assertions.assertEquals((long) Integer.MAX_VALUE + 1L, TEST.addAndGet(Integer.MAX_VALUE, 0L, 1L));
        Assertions.assertEquals(0L, TEST.addAndGet(Integer.MAX_VALUE, 1L, 0L));
        Assertions.assertEquals(1L, TEST.addAndGet(Integer.MAX_VALUE, 1L, 1L));
    }

    @Test
    public void TEST_10() {
        Assertions.assertEquals((long) Integer.MAX_VALUE + 1L, TEST.incrementAndGet(Integer.MAX_VALUE, 0L));
        Assertions.assertEquals(1L, TEST.incrementAndGet(Integer.MAX_VALUE, 1L));
    }

    @Test
    public void TEST_11() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.getAndIncrement(Integer.MAX_VALUE, 0L));
        Assertions.assertEquals(0L, TEST.getAndIncrement(Integer.MAX_VALUE, 1L));
    }

    @Test
    public void TEST_12() {
        Assertions.assertEquals((long) Integer.MAX_VALUE - 1L, TEST.decrementAndGet(Integer.MAX_VALUE | (1L << 32), 0L));
        Assertions.assertEquals(0L, TEST.decrementAndGet(Integer.MAX_VALUE | (1L << 32), 1L));
    }

    @Test
    public void TEST_13() {
        Assertions.assertEquals(Integer.MAX_VALUE, TEST.getAndDecrement(Integer.MAX_VALUE | (1L << 32), 0L));
        Assertions.assertEquals(1L, TEST.getAndDecrement(Integer.MAX_VALUE | (1L << 32), 1L));
    }

    @Test
    public void TEST_14() {
        Assertions.assertTrue(BitManipulatorSupport.create(64) instanceof BitManipulatorSupport64);
        Assertions.assertTrue(BitManipulatorSupport.create(63) instanceof BitManipulatorSupportDefault);
        Assertions.assertEquals(63, ((BitManipulatorSupportDefault) BitManipulatorSupport.create(63)).getUnitTest().getBits());
    }

    private static final class BitManipulatorSupportMock implements BitManipulatorSupport {
        @Override
        public long size() {
            return 2L;
        }

        @Override
        public boolean isOutOfBounds(final long value) {
            return value < 0L || value >= (long) Integer.MAX_VALUE;
        }

        @Override
        public long extract(final long raw, final long offset) {
            if (offset == 0L) {
                return (int) raw;
            }

            return raw >> 32;
        }

        @Override
        public long merge(final long raw, final long offset, final long value) {
            if (offset == 0L) {
                return raw & ((raw >> 31) << 31) | (long) (int) value;
            }

            return raw & ((raw << 31) >> 31) | (((long) (int) value) << 32);
        }
    }
}
