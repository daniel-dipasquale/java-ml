package com.java.util.concurrent.atomic;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerTest {
    private static final int COUNT = 4;
    private static final AtomicInteger TEST = new AtomicInteger();

    private static int incrementAndGet() {
        return TEST.getAndAccumulate(0, (o, n) -> (o + 1) % COUNT);
    }

    @Test
    @Ignore
    public void TEST_1() {
        Assert.assertEquals(0, TEST.get());
        Assert.assertEquals(0, incrementAndGet());
        Assert.assertEquals(1, TEST.get());
        Assert.assertEquals(1, incrementAndGet());
        Assert.assertEquals(2, TEST.get());
        Assert.assertEquals(2, incrementAndGet());
        Assert.assertEquals(3, TEST.get());
        Assert.assertEquals(3, incrementAndGet());
        Assert.assertEquals(0, TEST.get());
        Assert.assertEquals(0, incrementAndGet());
    }
}
