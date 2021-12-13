package com.dipasquale.ai.common.function.activation;

import com.dipasquale.io.serialization.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class IdentityActivationFunctionTest {
    private static final IdentityActivationFunction TEST = IdentityActivationFunction.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0f, TEST.forward(0f), 0f);
        Assertions.assertEquals(1f, TEST.forward(1f), 0f);
        Assertions.assertEquals(-1f, TEST.forward(-1f), 0f);
        Assertions.assertEquals(2f, TEST.forward(2f), 0f);
        Assertions.assertEquals(-2f, TEST.forward(-2f), 0f);
        Assertions.assertEquals(10f, TEST.forward(10f), 0f);
        Assertions.assertEquals(-10f, TEST.forward(-10f), 0f);
        Assertions.assertEquals(16f, TEST.forward(16f), 0f);
        Assertions.assertEquals(-16f, TEST.forward(-16f), 0f);
        Assertions.assertEquals(50f, TEST.forward(50f), 0f);
        Assertions.assertEquals(-50f, TEST.forward(-50f), 0f);
        Assertions.assertEquals(75f, TEST.forward(75f), 0f);
        Assertions.assertEquals(-75f, TEST.forward(-75f), 0f);
        Assertions.assertEquals(89f, TEST.forward(89f), 0f);
        Assertions.assertEquals(-89f, TEST.forward(-89f), 0f);
        Assertions.assertEquals(90f, TEST.forward(90f), 0f);
        Assertions.assertEquals(-90f, TEST.forward(-90f), 0f);
        Assertions.assertEquals(100f, TEST.forward(100f), 0f);
        Assertions.assertEquals(-100f, TEST.forward(-100f), 0f);
        Assertions.assertEquals(Float.MAX_VALUE, TEST.forward(Float.MAX_VALUE), 0f);
        Assertions.assertEquals(-Float.MAX_VALUE, TEST.forward(-Float.MAX_VALUE), 0f);
        Assertions.assertEquals(Float.POSITIVE_INFINITY, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assertions.assertEquals(Float.NEGATIVE_INFINITY, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assertions.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals("Identity", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableSupport.serializeObject(TEST);
            IdentityActivationFunction result = SerializableSupport.deserializeObject(activationFunction);

            Assertions.assertSame(TEST, result);
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }
}
