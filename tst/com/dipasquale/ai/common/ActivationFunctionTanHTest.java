package com.dipasquale.ai.common;

import org.junit.Assert;
import org.junit.Test;

public final class ActivationFunctionTanHTest {
    private static final ActivationFunctionTanH TEST = ActivationFunctionTanH.getInstance();

    @Test
    public void TEST_1() {
        Assert.assertEquals(0.0f, TEST.forward(0f), 0f);
        Assert.assertEquals(0.7615942f, TEST.forward(1f), 0f);
        Assert.assertEquals(-0.7615942f, TEST.forward(-1f), 0f);
        Assert.assertEquals(0.9640276f, TEST.forward(2f), 0f);
        Assert.assertEquals(-0.9640276f, TEST.forward(-2f), 0f);
        Assert.assertEquals(1f, TEST.forward(10f), 0f);
        Assert.assertEquals(1f, TEST.forward(16f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-16f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-10f), 0f);
        Assert.assertEquals(1f, TEST.forward(50f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-50f), 0f);
        Assert.assertEquals(1f, TEST.forward(75f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-75f), 0f);
        Assert.assertEquals(1f, TEST.forward(89f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-89f), 0f);
        Assert.assertEquals(1f, TEST.forward(90f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-90f), 0f);
        Assert.assertEquals(1f, TEST.forward(100f), 0f);
        Assert.assertEquals(-1f, TEST.forward(-100f), 0f);
        Assert.assertEquals(1f, TEST.forward(Float.MAX_VALUE), 0f);
        Assert.assertEquals(-1f, TEST.forward(-Float.MAX_VALUE), 0f);
        Assert.assertEquals(1f, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assert.assertEquals(-1f, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assert.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals("TanH", TEST.toString());
    }
}
