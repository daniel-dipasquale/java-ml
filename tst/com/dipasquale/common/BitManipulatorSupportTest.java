package com.dipasquale.common;

import org.junit.Assert;
import org.junit.Test;

public final class BitManipulatorSupportTest {
    private static final BitManipulatorSupport TEST = new BitManipulatorSupportMock();

    @Test
    public void TEST_1() {
        Assert.assertEquals(2L, TEST.size());
    }

    @Test
    public void TEST_2() {
        Assert.assertTrue(TEST.isOutOfBounds((long) Integer.MAX_VALUE + 1L));
        Assert.assertFalse(TEST.isOutOfBounds(0L));
        Assert.assertTrue(TEST.isOutOfBounds(-1L));
    }

    @Test
    public void TEST_3() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.extract(Integer.MAX_VALUE | 1L << 32, 0L));
        Assert.assertEquals(1L, TEST.extract(Integer.MAX_VALUE | 1L << 32, 1L));
    }

    @Test
    public void TEST_4() {
        Assert.assertEquals(Integer.MIN_VALUE, TEST.merge(Integer.MAX_VALUE, 0L, Integer.MIN_VALUE));
        Assert.assertEquals(Integer.MAX_VALUE | (1L << 32), TEST.merge(Integer.MAX_VALUE, 1L, 1L));
    }

    @Test
    public void TEST_5() {
        Assert.assertEquals(1L, TEST.setAndGet(Integer.MAX_VALUE, 0L, 1L));
        Assert.assertEquals(2L, TEST.setAndGet(Integer.MAX_VALUE, 1L, 2L));
    }

    @Test
    public void TEST_6() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.getAndSet(Integer.MAX_VALUE, 0L, 1L));
        Assert.assertEquals(0L, TEST.getAndSet(Integer.MAX_VALUE, 1L, 2L));
    }

    @Test
    public void TEST_7() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.accumulateAndGet(Integer.MAX_VALUE, 0L, 1L, (o, n) -> o));
        Assert.assertEquals(1L, TEST.accumulateAndGet(Integer.MAX_VALUE, 0L, 1L, (o, n) -> n));
        Assert.assertEquals(0L, TEST.accumulateAndGet(Integer.MAX_VALUE, 1L, 2L, (o, n) -> o));
        Assert.assertEquals(2L, TEST.accumulateAndGet(Integer.MAX_VALUE, 1L, 2L, (o, n) -> n));
    }

    @Test
    public void TEST_8() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.getAndAccumulate(Integer.MAX_VALUE, 0L, 1L, (o, n) -> o));
        Assert.assertEquals(Integer.MAX_VALUE, TEST.getAndAccumulate(Integer.MAX_VALUE, 0L, 1L, (o, n) -> n));
        Assert.assertEquals(0L, TEST.getAndAccumulate(Integer.MAX_VALUE, 1L, 2L, (o, n) -> o));
        Assert.assertEquals(0L, TEST.getAndAccumulate(Integer.MAX_VALUE, 1L, 2L, (o, n) -> n));
    }

    @Test
    public void TEST_9() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.addAndGet(Integer.MAX_VALUE, 0L, 0L));
        Assert.assertEquals((long) Integer.MAX_VALUE + 1L, TEST.addAndGet(Integer.MAX_VALUE, 0L, 1L));
        Assert.assertEquals(0L, TEST.addAndGet(Integer.MAX_VALUE, 1L, 0L));
        Assert.assertEquals(1L, TEST.addAndGet(Integer.MAX_VALUE, 1L, 1L));
    }

    @Test
    public void TEST_10() {
        Assert.assertEquals((long) Integer.MAX_VALUE + 1L, TEST.incrementAndGet(Integer.MAX_VALUE, 0L));
        Assert.assertEquals(1L, TEST.incrementAndGet(Integer.MAX_VALUE, 1L));
    }

    @Test
    public void TEST_11() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.getAndIncrement(Integer.MAX_VALUE, 0L));
        Assert.assertEquals(0L, TEST.getAndIncrement(Integer.MAX_VALUE, 1L));
    }

    @Test
    public void TEST_12() {
        Assert.assertEquals((long) Integer.MAX_VALUE - 1L, TEST.decrementAndGet(Integer.MAX_VALUE | (1L << 32), 0L));
        Assert.assertEquals(0L, TEST.decrementAndGet(Integer.MAX_VALUE | (1L << 32), 1L));
    }

    @Test
    public void TEST_13() {
        Assert.assertEquals(Integer.MAX_VALUE, TEST.getAndDecrement(Integer.MAX_VALUE | (1L << 32), 0L));
        Assert.assertEquals(1L, TEST.getAndDecrement(Integer.MAX_VALUE | (1L << 32), 1L));
    }

    @Test
    public void TEST_14() {
        Assert.assertTrue(BitManipulatorSupport.create(64) instanceof BitManipulatorSupport64);
        Assert.assertTrue(BitManipulatorSupport.create(63) instanceof BitManipulatorSupportDefault a && a.getUnitTest().getBits() == 63);
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
