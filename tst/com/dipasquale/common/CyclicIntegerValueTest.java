package com.dipasquale.common;

import org.junit.jupiter.api.Test;

public final class CyclicIntegerValueTest {
    private static final CyclicIntegerValueTestHarness HARNESS = new CyclicIntegerValueTestHarness(new CyclicIntegerValueFactory());

    @Test
    public void TEST_1() {
        HARNESS.assertInitialState();
    }

    @Test
    public void TEST_2() {
        HARNESS.assertMultiCycleIncrement();
    }

    @Test
    public void TEST_3() {
        HARNESS.assertMultiCycleDecrement();
    }

    @Test
    public void TEST_4() {
        HARNESS.assertMultiCycleIncrementDecrement();
    }

    @Test
    public void TEST_5() {
        HARNESS.assertMultiCycleReadWrite();
    }

    @Test
    public void TEST_6() {
        HARNESS.assertCompareTo();
    }

    @Test
    public void TEST_7() {
        HARNESS.assertEqualsAndHashCode();
    }

    @Test
    public void TEST_8() {
        HARNESS.assertToString();
    }

    @Test
    public void TEST_9() {
        HARNESS.assertSerialization();
    }

    private static final class CyclicIntegerValueFactory implements IntegerValueFactory {
        @Override
        public IntegerValue create(final int maximum, final int offset) {
            return new CyclicIntegerValue(maximum, offset);
        }

        @Override
        public IntegerValue create(final int maximum) {
            return new CyclicIntegerValue(maximum);
        }
    }
}
