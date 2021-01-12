package com.java.lang;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public final class OperatorPriorityTest {
    @Test
    @Ignore
    public void TEST_1() {
        long x = 10;
        long y = 187;
        long z = 180;

        x -= y - z;

        Assert.assertEquals(3, x);
    }
}
