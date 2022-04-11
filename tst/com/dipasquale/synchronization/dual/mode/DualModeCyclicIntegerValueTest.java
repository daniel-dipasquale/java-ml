package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.CyclicIntegerValueTestHarness;
import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.IntegerValueFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DualModeCyclicIntegerValueTest {
    private static final CyclicIntegerValueTestHarness STANDARD_HARNESS = new CyclicIntegerValueTestHarness(new DualModeCyclicIntegerValueFactory(0));
    private static final CyclicIntegerValueTestHarness CONCURRENT_HARNESS = new CyclicIntegerValueTestHarness(new DualModeCyclicIntegerValueFactory(1));

    @Test
    public void TEST_1() {
        STANDARD_HARNESS.assertInitialState();
        CONCURRENT_HARNESS.assertInitialState();
    }

    @Test
    public void TEST_2() {
        STANDARD_HARNESS.assertMultiCycleIncrement();
        CONCURRENT_HARNESS.assertMultiCycleIncrement();
    }

    @Test
    public void TEST_3() {
        STANDARD_HARNESS.assertMultiCycleDecrement();
        CONCURRENT_HARNESS.assertMultiCycleDecrement();
    }

    @Test
    public void TEST_4() {
        STANDARD_HARNESS.assertMultiCycleIncrementDecrement();
        CONCURRENT_HARNESS.assertMultiCycleIncrementDecrement();
    }

    @Test
    public void TEST_5() {
        STANDARD_HARNESS.assertMultiCycleReadWrite();
        CONCURRENT_HARNESS.assertMultiCycleReadWrite();
    }

    @Test
    public void TEST_6() {
        STANDARD_HARNESS.assertCompareTo();
        CONCURRENT_HARNESS.assertCompareTo();
    }

    @Test
    public void TEST_7() {
        STANDARD_HARNESS.assertEqualsAndHashCode();
        CONCURRENT_HARNESS.assertEqualsAndHashCode();
    }

    @Test
    public void TEST_8() {
        STANDARD_HARNESS.assertToString();
        CONCURRENT_HARNESS.assertToString();
    }

    @Test
    public void TEST_9() {
        STANDARD_HARNESS.assertSerialization();
        CONCURRENT_HARNESS.assertSerialization();
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
        public IntegerValue create(final int maximum, final int offset) {
            return new DualModeCyclicIntegerValue(concurrencyLevel, maximum, offset);
        }

        @Override
        public IntegerValue create(final int maximum) {
            return new DualModeCyclicIntegerValue(concurrencyLevel, maximum);
        }
    }
}
