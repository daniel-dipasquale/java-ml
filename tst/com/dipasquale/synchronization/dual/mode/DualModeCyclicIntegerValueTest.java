package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.CyclicIntegerValueTestHarness;
import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.IntegerValueFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DualModeCyclicIntegerValueTest {
    private static final CyclicIntegerValueTestHarness PLAIN_HARNESS = new CyclicIntegerValueTestHarness(new DualModeCyclicIntegerValueFactory(0));
    private static final CyclicIntegerValueTestHarness ATOMIC_HARNESS = new CyclicIntegerValueTestHarness(new DualModeCyclicIntegerValueFactory(1));

    @Test
    public void TEST_1() {
        PLAIN_HARNESS.assertInitialState();
        ATOMIC_HARNESS.assertInitialState();
    }

    @Test
    public void TEST_2() {
        PLAIN_HARNESS.assertMultiCycleIncrement();
        ATOMIC_HARNESS.assertMultiCycleIncrement();
    }

    @Test
    public void TEST_3() {
        PLAIN_HARNESS.assertMultiCycleDecrement();
        ATOMIC_HARNESS.assertMultiCycleDecrement();
    }

    @Test
    public void TEST_4() {
        PLAIN_HARNESS.assertMultiCycleIncrementDecrement();
        ATOMIC_HARNESS.assertMultiCycleIncrementDecrement();
    }

    @Test
    public void TEST_5() {
        PLAIN_HARNESS.assertMultiCycleReadWrite();
        ATOMIC_HARNESS.assertMultiCycleReadWrite();
    }

    @Test
    public void TEST_6() {
        PLAIN_HARNESS.assertCompareTo();
        ATOMIC_HARNESS.assertCompareTo();
    }

    @Test
    public void TEST_7() {
        PLAIN_HARNESS.assertEqualsAndHashCode();
        ATOMIC_HARNESS.assertEqualsAndHashCode();
    }

    @Test
    public void TEST_8() {
        PLAIN_HARNESS.assertToString();
        ATOMIC_HARNESS.assertToString();
    }

    @Test
    public void TEST_9() {
        PLAIN_HARNESS.assertSerialization();
        ATOMIC_HARNESS.assertSerialization();
    }

    private void assertActivateMode(final DualModeCyclicIntegerValue integerValue, int current) {
        Assertions.assertEquals(current, integerValue.current());
        integerValue.activateMode(1);
        Assertions.assertEquals(current, integerValue.current());
    }

    @Test
    public void TEST_10() {
        DualModeCyclicIntegerValue test1 = new DualModeCyclicIntegerValue(0, 10, -2);
        DualModeCyclicIntegerValue test2 = new DualModeCyclicIntegerValue(0, 10, -1);
        DualModeCyclicIntegerValue test3 = new DualModeCyclicIntegerValue(0, 10, 0);
        DualModeCyclicIntegerValue test4 = new DualModeCyclicIntegerValue(0, 10, 1);
        DualModeCyclicIntegerValue test5 = new DualModeCyclicIntegerValue(0, 10, 2);

        assertActivateMode(test1, 8);
        assertActivateMode(test2, 9);
        assertActivateMode(test3, 0);
        assertActivateMode(test4, 1);
        assertActivateMode(test5, 2);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeCyclicIntegerValueFactory implements IntegerValueFactory {
        private final int concurrencyLevel;

        @Override
        public IntegerValue create(final int max, final int offset) {
            return new DualModeCyclicIntegerValue(concurrencyLevel, max, offset);
        }

        @Override
        public IntegerValue create(final int max) {
            return new DualModeCyclicIntegerValue(concurrencyLevel, max);
        }
    }
}
