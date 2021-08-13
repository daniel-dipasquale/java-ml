/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.bit;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

public final class BitManipulatorTest {
    private static final AtomicLong SIZE = new AtomicLong();
    private static final AtomicLong VALUE = new AtomicLong();
    private static final BitManipulator TEST = new BitManipulatorMock(SIZE, VALUE);

    @BeforeEach
    public void beforeEach() {
        SIZE.set(0L);
        VALUE.set(1L);
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(SIZE.get(), TEST.size());
        Assertions.assertEquals(SIZE.incrementAndGet(), TEST.size());
    }

    @Test
    public void TEST_2() {
        Assertions.assertFalse(TEST.isOutOfBounds(0L));
        Assertions.assertTrue(TEST.isOutOfBounds(65L));
    }

    @Test
    public void TEST_3() {
        Assertions.assertEquals(1L, TEST.extract(0L));
        Assertions.assertEquals(65L, TEST.extract(64L));
    }

    @Test
    public void TEST_4() {
        Assertions.assertEquals(6L, TEST.merge(2L, 4L));
        Assertions.assertEquals(6L, VALUE.get());
        Assertions.assertEquals(12L, TEST.merge(5L, 7L));
        Assertions.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_5() {
        Assertions.assertEquals(8L, TEST.setAndGet(2L, 4L));
        Assertions.assertEquals(6L, VALUE.get());
        Assertions.assertEquals(17L, TEST.setAndGet(5L, 7L));
        Assertions.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_6() {
        Assertions.assertEquals(3L, TEST.getAndSet(2L, 4L));
        Assertions.assertEquals(6L, VALUE.get());
        Assertions.assertEquals(11L, TEST.getAndSet(5L, 7L));
        Assertions.assertEquals(12L, VALUE.get());
    }

    @Test
    public void TEST_7() {
        Assertions.assertEquals(11L, TEST.accumulateAndGet(2L, 4L, Long::sum));
        Assertions.assertEquals(9L, VALUE.get());
        Assertions.assertEquals(31L, TEST.accumulateAndGet(5L, 7L, Long::sum));
        Assertions.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_8() {
        Assertions.assertEquals(3L, TEST.getAndAccumulate(2L, 4L, Long::sum));
        Assertions.assertEquals(9L, VALUE.get());
        Assertions.assertEquals(14L, TEST.getAndAccumulate(5L, 7L, Long::sum));
        Assertions.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_9() {
        Assertions.assertEquals(11L, TEST.addAndGet(2L, 4L));
        Assertions.assertEquals(9L, VALUE.get());
        Assertions.assertEquals(31L, TEST.addAndGet(5L, 7L));
        Assertions.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_10() {
        Assertions.assertEquals(3L, TEST.getAndAdd(2L, 4L));
        Assertions.assertEquals(9L, VALUE.get());
        Assertions.assertEquals(14L, TEST.getAndAdd(5L, 7L));
        Assertions.assertEquals(26L, VALUE.get());
    }

    @Test
    public void TEST_11() {
        Assertions.assertEquals(8L, TEST.incrementAndGet(2L));
        Assertions.assertEquals(6L, VALUE.get());
        Assertions.assertEquals(22L, TEST.incrementAndGet(5L));
        Assertions.assertEquals(17L, VALUE.get());
    }

    @Test
    public void TEST_12() {
        Assertions.assertEquals(3L, TEST.getAndIncrement(2L));
        Assertions.assertEquals(6L, VALUE.get());
        Assertions.assertEquals(11L, TEST.getAndIncrement(5L));
        Assertions.assertEquals(17L, VALUE.get());
    }

    @Test
    public void TEST_13() {
        Assertions.assertEquals(6L, TEST.decrementAndGet(2L));
        Assertions.assertEquals(4L, VALUE.get());
        Assertions.assertEquals(18L, TEST.decrementAndGet(5L));
        Assertions.assertEquals(13L, VALUE.get());
    }

    @Test
    public void TEST_14() {
        Assertions.assertEquals(3L, TEST.getAndDecrement(2L));
        Assertions.assertEquals(4L, VALUE.get());
        Assertions.assertEquals(9L, TEST.getAndDecrement(5L));
        Assertions.assertEquals(13L, VALUE.get());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BitManipulatorMock implements BitManipulator {
        private final AtomicLong size;
        private final AtomicLong value;

        @Override
        public long size() {
            return size.get();
        }

        @Override
        public boolean isOutOfBounds(final long value) {
            return value < 0L || value > 64L;
        }

        @Override
        public long extract(final long offset) {
            return value.get() + offset;
        }

        @Override
        public long merge(final long offset, final long value) {
            this.value.set(offset + value);

            return this.value.get();
        }
    }
}
