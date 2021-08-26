package com.dipasquale.common.concurrent;

import com.dipasquale.common.CyclicIntegerCounterTestHarness;
import com.dipasquale.common.IntegerCounter;
import com.dipasquale.common.IntegerCounterFactory;
import org.junit.jupiter.api.Test;

public final class AtomicCyclicIntegerCounterTest {
    private static final CyclicIntegerCounterTestHarness HARNESS = new CyclicIntegerCounterTestHarness(new AtomicCyclicIntegerCounterFactory());

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

    private static final class AtomicCyclicIntegerCounterFactory implements IntegerCounterFactory {
        @Override
        public IntegerCounter create(final int max, final int offset) {
            return new AtomicCyclicIntegerCounter(max, offset);
        }

        @Override
        public IntegerCounter create(final int max) {
            return new AtomicCyclicIntegerCounter(max);
        }
    }
}
