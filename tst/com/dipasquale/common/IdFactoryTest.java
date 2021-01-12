package com.dipasquale.common;

import org.junit.Assert;
import org.junit.Test;

public final class IdFactoryTest {
    private static final IdFactory<Long> TEST = IdFactory.createThreadIdFactory();

    @Test
    public void TEST() {
        Assert.assertEquals(Long.valueOf(Thread.currentThread().getId()), TEST.createId());
    }
}
