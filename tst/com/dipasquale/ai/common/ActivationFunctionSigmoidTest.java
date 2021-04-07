package com.dipasquale.ai.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ActivationFunctionSigmoidTest {
    private static final ActivationFunctionSigmoid TEST = ActivationFunctionSigmoid.getInstance();

    @Test
    public void TEST_1() {
        Assert.assertEquals(0.5f, TEST.forward(0f), 0f);
        Assert.assertEquals(0.7310586f, TEST.forward(1f), 0f);
        Assert.assertEquals(0.26894143f, TEST.forward(-1f), 0f);
        Assert.assertEquals(0.880797f, TEST.forward(2f), 0f);
        Assert.assertEquals(0.11920292f, TEST.forward(-2f), 0f);
        Assert.assertEquals(0.9999546f, TEST.forward(10f), 0f);
        Assert.assertEquals(4.5397872E-5f, TEST.forward(-10f), 0f);
        Assert.assertEquals(0.9999999f, TEST.forward(16f), 0f);
        Assert.assertEquals(1.12535155E-7f, TEST.forward(-16f), 0f);
        Assert.assertEquals(1f, TEST.forward(50f), 0f);
        Assert.assertEquals(1.9287499E-22f, TEST.forward(-50f), 0f);
        Assert.assertEquals(1f, TEST.forward(75f), 0f);
        Assert.assertEquals(2.678637E-33f, TEST.forward(-75f), 0f);
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
        Assert.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals("Sigmoid", TEST.toString());
    }

    private static byte[] serialize(final ActivationFunctionSigmoid activationFunction)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(activationFunction);

            return outputStream.toByteArray();
        }
    }

    private static ActivationFunctionSigmoid deserialize(final byte[] activationFunction)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(activationFunction);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (ActivationFunctionSigmoid) objectInputStream.readObject();
        }
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = serialize(TEST);
            ActivationFunctionSigmoid result = deserialize(activationFunction);

            Assert.assertSame(TEST, result);
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }
}
