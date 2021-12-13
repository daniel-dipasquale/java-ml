package com.dipasquale.ai.common.function.activation;

import com.dipasquale.io.serialization.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SteepenedSigmoidActivationFunctionTest {
    private static final SteepenedSigmoidActivationFunction TEST = SteepenedSigmoidActivationFunction.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0.5f, TEST.forward(0f), 0f);
        Assertions.assertEquals(0.9926085f, TEST.forward(1f), 0f);
        Assertions.assertEquals(0.0073915403f, TEST.forward(-1f), 0f);
        Assertions.assertEquals(0.99994457f, TEST.forward(2f), 0f);
        Assertions.assertEquals(5.5448516E-5f, TEST.forward(-2f), 0f);
        Assertions.assertEquals(1f, TEST.forward(10f), 0f);
        Assertions.assertEquals(5.2428857E-22f, TEST.forward(-10f), 0f);
        Assertions.assertEquals(1f, TEST.forward(16f), 0f);
        Assertions.assertEquals(8.939474E-35f, TEST.forward(-16f), 0f);
        Assertions.assertEquals(1f, TEST.forward(50f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-50f), 0f);
        Assertions.assertEquals(1f, TEST.forward(75f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-75f), 0f);
        Assertions.assertEquals(1f, TEST.forward(89f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-89f), 0f);
        Assertions.assertEquals(1f, TEST.forward(90f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-90f), 0f);
        Assertions.assertEquals(1f, TEST.forward(100f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-100f), 0f);
        Assertions.assertEquals(1f, TEST.forward(Float.MAX_VALUE), 0f);
        Assertions.assertEquals(0f, TEST.forward(-Float.MAX_VALUE), 0f);
        Assertions.assertEquals(1f, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assertions.assertEquals(0f, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assertions.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals("SteepenedSigmoid", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableSupport.serializeObject(TEST);
            SteepenedSigmoidActivationFunction result = SerializableSupport.deserializeObject(activationFunction);

            Assertions.assertSame(TEST, result);
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }
}
