package com.dipasquale.ai.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ActivationFunctionStepTest {
    private static final ActivationFunctionStep TEST = ActivationFunctionStep.getInstance();

    @Test
    public void TEST_1() {
        Assert.assertEquals(1f, TEST.forward(0f), 0f);
        Assert.assertEquals(1f, TEST.forward(1f), 0f);
        Assert.assertEquals(0f, TEST.forward(-1f), 0f);
        Assert.assertEquals(1f, TEST.forward(2f), 0f);
        Assert.assertEquals(0f, TEST.forward(-2f), 0f);
        Assert.assertEquals(1f, TEST.forward(10f), 0f);
        Assert.assertEquals(0f, TEST.forward(-10f), 0f);
        Assert.assertEquals(1f, TEST.forward(16f), 0f);
        Assert.assertEquals(0f, TEST.forward(-16f), 0f);
        Assert.assertEquals(1f, TEST.forward(50f), 0f);
        Assert.assertEquals(0f, TEST.forward(-50f), 0f);
        Assert.assertEquals(1f, TEST.forward(75f), 0f);
        Assert.assertEquals(0f, TEST.forward(-75f), 0f);
        Assert.assertEquals(1f, TEST.forward(89f), 0f);
        Assert.assertEquals(0f, TEST.forward(-89f), 0f);
        Assert.assertEquals(1f, TEST.forward(90f), 0f);
        Assert.assertEquals(0f, TEST.forward(-90f), 0f);
        Assert.assertEquals(1f, TEST.forward(100f), 0f);
        Assert.assertEquals(0f, TEST.forward(-100f), 0f);
        Assert.assertEquals(1f, TEST.forward(Float.MAX_VALUE), 0f);
        Assert.assertEquals(0f, TEST.forward(-Float.MAX_VALUE), 0f);
        Assert.assertEquals(1f, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assert.assertEquals(0f, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assert.assertEquals(0f, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals("Step", TEST.toString());
    }

    private static byte[] serialize(final ActivationFunctionStep activationFunction)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(activationFunction);

            return outputStream.toByteArray();
        }
    }

    private static ActivationFunctionStep deserialize(final byte[] activationFunction)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(activationFunction);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (ActivationFunctionStep) objectInputStream.readObject();
        }
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = serialize(TEST);
            ActivationFunctionStep result = deserialize(activationFunction);

            Assert.assertSame(TEST, result);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }
}
