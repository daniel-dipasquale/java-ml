package com.dipasquale.common;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

public final class BitManipulatorTest {
    private static final AtomicLong SIZE = new AtomicLong();
    private static final AtomicLong VALUE = new AtomicLong();
    private static final BitManipulator TEST = new BitManipulatorMock(SIZE, VALUE);

    @Before
    public void before() {
        SIZE.set(0L);
        VALUE.set(1L);
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(SIZE.get(), TEST.size());
        Assert.assertEquals(SIZE.incrementAndGet(), TEST.size());
    }

    @Test
    public void TEST_2() {
        Assert.assertFalse(TEST.isOutOfBounds(0L));
        Assert.assertTrue(TEST.isOutOfBounds(65L));
    }

    @Test
    public void TEST_3() {
        Assert.assertEquals(1L, TEST.extract(0L));
        Assert.assertEquals(65L, TEST.extract(64L));
    }

    @Test
    public void TEST_4() {
        Assert.assertEquals(6L, TEST.merge(2L, 4L));
        Assert.assertEquals(6L, VALUE.get());
        Assert.assertEquals(12L, TEST.merge(5L, 7L));
        Assert.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_5() {
        Assert.assertEquals(4L, TEST.setAndGet(2L, 4L));
        Assert.assertEquals(6L, VALUE.get());
        Assert.assertEquals(7L, TEST.setAndGet(5L, 7L));
        Assert.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_6() {
        Assert.assertEquals(3L, TEST.getAndSet(2L, 4L));
        Assert.assertEquals(6L, VALUE.get());
        Assert.assertEquals(11L, TEST.getAndSet(5L, 7L));
        Assert.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_7() {
        Assert.assertEquals(7L, TEST.accumulateAndGet(2L, 4L, Long::sum));
        Assert.assertEquals(9L, VALUE.get());
        Assert.assertEquals(21L, TEST.accumulateAndGet(5L, 7L, Long::sum));
        Assert.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_8() {
        Assert.assertEquals(3L, TEST.getAndAccumulate(2L, 4L, Long::sum));
        Assert.assertEquals(9L, VALUE.get());
        Assert.assertEquals(14L, TEST.getAndAccumulate(5L, 7L, Long::sum));
        Assert.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_9() {
        Assert.assertEquals(7L, TEST.addAndGet(2L, 4L));
        Assert.assertEquals(9L, VALUE.get());
        Assert.assertEquals(21L, TEST.addAndGet(5L, 7L));
        Assert.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_10() {
        Assert.assertEquals(3L, TEST.getAndAdd(2L, 4L));
        Assert.assertEquals(9L, VALUE.get());
        Assert.assertEquals(14L, TEST.getAndAdd(5L, 7L));
        Assert.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_11() {
        Assert.assertEquals(4L, TEST.incrementAndGet(2L));
        Assert.assertEquals(6L, VALUE.get());
        Assert.assertEquals(12L, TEST.incrementAndGet(5L));
        Assert.assertEquals(17L, VALUE.get());
    }

    @Test
    public void TEST_12() {
        Assert.assertEquals(3L, TEST.getAndIncrement(2L));
        Assert.assertEquals(6L, VALUE.get());
        Assert.assertEquals(11L, TEST.getAndIncrement(5L));
        Assert.assertEquals(17L, VALUE.get());
    }

    @Test
    public void TEST_13() {
        Assert.assertEquals(2L, TEST.decrementAndGet(2L));
        Assert.assertEquals(4L, VALUE.get());
        Assert.assertEquals(8L, TEST.decrementAndGet(5L));
        Assert.assertEquals(13L, VALUE.get());
    }

    @Test
    public void TEST_14() {
        Assert.assertEquals(3L, TEST.getAndDecrement(2L));
        Assert.assertEquals(4L, VALUE.get());
        Assert.assertEquals(9L, TEST.getAndDecrement(5L));
        Assert.assertEquals(13L, VALUE.get());
    }

    @RequiredArgsConstructor
    private static final class BitManipulatorMock implements BitManipulator {
        private final AtomicLong sizeCas;
        private final AtomicLong valueCas;

        @Override
        public long size() {
            return sizeCas.get();
        }

        @Override
        public boolean isOutOfBounds(final long value) {
            return value > 64L;
        }

        @Override
        public long extract(final long offset) {
            return valueCas.get() + offset;
        }

        @Override
        public long merge(final long offset, final long value) {
            valueCas.set(offset + value);

            return valueCas.get();
        }
    }
}
