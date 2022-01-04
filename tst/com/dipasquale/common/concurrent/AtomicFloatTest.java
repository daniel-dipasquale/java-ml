package com.dipasquale.common.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class AtomicFloatTest {
    @Test
    public void TEST_1() {
        AtomicFloat test = new AtomicFloat(0f);

        Assertions.assertEquals(0f, test.get());
    }

    @Test
    public void TEST_2() {
        AtomicFloat test = new AtomicFloat(0f);

        test.set(1f);

        Assertions.assertEquals(1f, test.get());
    }

    @Test
    public void TEST_3() {
        AtomicFloat test = new AtomicFloat(0f);

        Assertions.assertEquals(1f, test.addAndGet(1f));
        Assertions.assertEquals(1.5f, test.addAndGet(0.5f));
        Assertions.assertEquals(1.25f, test.addAndGet(-0.25f));
    }
}
