/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.java.util.concurrent.atomic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerTest {
    private static final int COUNT = 4;
    private static final AtomicInteger TEST = new AtomicInteger();

    private static int incrementAndGet() {
        return TEST.getAndAccumulate(0, (o, n) -> (o + 1) % COUNT);
    }

    @Test
    @Disabled
    public void TEST_1() {
        Assertions.assertEquals(0, TEST.get());
        Assertions.assertEquals(0, incrementAndGet());
        Assertions.assertEquals(1, TEST.get());
        Assertions.assertEquals(1, incrementAndGet());
        Assertions.assertEquals(2, TEST.get());
        Assertions.assertEquals(2, incrementAndGet());
        Assertions.assertEquals(3, TEST.get());
        Assertions.assertEquals(3, incrementAndGet());
        Assertions.assertEquals(0, TEST.get());
        Assertions.assertEquals(0, incrementAndGet());
    }
}
