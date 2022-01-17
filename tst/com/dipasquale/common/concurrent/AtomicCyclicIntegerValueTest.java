package com.dipasquale.common.concurrent;

import com.dipasquale.common.CyclicIntegerValueTestHarness;
import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.IntegerValueFactory;
import org.junit.jupiter.api.Test;

public final class AtomicCyclicIntegerValueTest {
    private static final CyclicIntegerValueTestHarness HARNESS = new CyclicIntegerValueTestHarness(new AtomicCyclicIntegerValueFactory());

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

    private static final class AtomicCyclicIntegerValueFactory implements IntegerValueFactory {
        @Override
        public IntegerValue create(final int maximum, final int offset) {
            return new AtomicCyclicIntegerValue(maximum, offset);
        }

        @Override
        public IntegerValue create(final int maximum) {
            return new AtomicCyclicIntegerValue(maximum);
        }
    }
}
