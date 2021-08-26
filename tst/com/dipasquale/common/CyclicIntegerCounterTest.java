package com.dipasquale.common;

import org.junit.jupiter.api.Test;

public final class CyclicIntegerCounterTest {
    private static final CyclicIntegerCounterTestHarness HARNESS = new CyclicIntegerCounterTestHarness(new CyclicIntegerCounterFactory());

    @Test
    public void TEST_1() {
        HARNESS.assertContinuousIncrementAndCurrent();
    }

    @Test
    public void TEST_2() {
        HARNESS.assertContinuousDecrementAndCurrent();
    }

    @Test
    public void TEST_3() {
        HARNESS.assertCyclicIncrementAndCurrent();
    }

    @Test
    public void TEST_4() {
        HARNESS.assertCurrentGetAndSet();
    }

    @Test
    public void TEST_5() {
        HARNESS.assertCompareTo();
    }

    @Test
    public void TEST_6() {
        HARNESS.assertEqualsAndHashCode();
    }

    @Test
    public void TEST_7() {
        HARNESS.assertToString();
    }

    @Test
    public void TEST_8() {
        HARNESS.assertSerialization();
    }

    private static final class CyclicIntegerCounterFactory implements IntegerCounterFactory {
        @Override
        public IntegerCounter create(final int max, final int offset) {
            return new CyclicIntegerCounter(max, offset);
        }

        @Override
        public IntegerCounter create(final int max) {
            return new CyclicIntegerCounter(max);
        }
    }
}
