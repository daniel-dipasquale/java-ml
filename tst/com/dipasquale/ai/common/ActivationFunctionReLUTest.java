package com.dipasquale.ai.common;

import com.dipasquale.common.test.SerializableUtils;
import org.junit.Assert;
import org.junit.Test;

public final class ActivationFunctionReLUTest {
    private static final ActivationFunctionReLU TEST = ActivationFunctionReLU.getInstance();

    @Test
    public void TEST_1() {
        Assert.assertEquals(0f, TEST.forward(0f), 0f);
        Assert.assertEquals(1f, TEST.forward(1f), 0f);
        Assert.assertEquals(0f, TEST.forward(-1f), 0f);
        Assert.assertEquals(2f, TEST.forward(2f), 0f);
        Assert.assertEquals(0f, TEST.forward(-2f), 0f);
        Assert.assertEquals(10f, TEST.forward(10f), 0f);
        Assert.assertEquals(0f, TEST.forward(-10f), 0f);
        Assert.assertEquals(16f, TEST.forward(16f), 0f);
        Assert.assertEquals(0f, TEST.forward(-16f), 0f);
        Assert.assertEquals(50f, TEST.forward(50f), 0f);
        Assert.assertEquals(0f, TEST.forward(-50f), 0f);
        Assert.assertEquals(75f, TEST.forward(75f), 0f);
        Assert.assertEquals(0f, TEST.forward(-75f), 0f);
        Assert.assertEquals(89f, TEST.forward(89f), 0f);
        Assert.assertEquals(0f, TEST.forward(-89f), 0f);
        Assert.assertEquals(90f, TEST.forward(90f), 0f);
        Assert.assertEquals(0f, TEST.forward(-90f), 0f);
        Assert.assertEquals(100f, TEST.forward(100f), 0f);
        Assert.assertEquals(0f, TEST.forward(-100f), 0f);
        Assert.assertEquals(Float.MAX_VALUE, TEST.forward(Float.MAX_VALUE), 0f);
        Assert.assertEquals(0f, TEST.forward(-Float.MAX_VALUE), 0f);
        Assert.assertEquals(Float.POSITIVE_INFINITY, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assert.assertEquals(0f, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assert.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals("ReLU", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableUtils.serialize(TEST);
            ActivationFunctionReLU result = SerializableUtils.deserialize(activationFunction);

            Assert.assertSame(TEST, result);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }
}
