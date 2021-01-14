package com.dipasquale.concurrent;

import org.junit.Assert;
import org.junit.Test;

public final class ConcurrentIdTest {
    @Test
    public void TEST_1() {
        Assert.assertEquals(0, ConcurrentId.create(0, 0, 1).compareTo(ConcurrentId.create(0, 0, 1)));
        Assert.assertEquals(1, ConcurrentId.create(0, 1, 0).compareTo(ConcurrentId.create(0, 0, 1)));
        Assert.assertEquals(1, ConcurrentId.create(1, 0, 0).compareTo(ConcurrentId.create(0, 1, 0)));
        Assert.assertEquals(-1, ConcurrentId.create(0, 0, 0).compareTo(ConcurrentId.create(1, 0, 0)));
        Assert.assertEquals(-1, ConcurrentId.create(0, 0, 1).compareTo(ConcurrentId.create(1, 0, 0)));
        Assert.assertEquals(-1, ConcurrentId.create(0, 0, 1).compareTo(ConcurrentId.create(0, 1, 0)));
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals(ConcurrentId.create(0, 0, 1), ConcurrentId.create(0, 0, 1));
        Assert.assertNotEquals(ConcurrentId.create(0, 0, 0), ConcurrentId.create(0, 0, 1));
    }

    @Test
    public void TEST_3() {
        Assert.assertEquals("0.0.1", ConcurrentId.create(0, 0, 1).toString());
    }
}
