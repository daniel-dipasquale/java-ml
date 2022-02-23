package com.dipasquale.ai.rl.neat.function.activation;

import com.dipasquale.io.serialization.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class StepActivationFunctionTest {
    private static final StepActivationFunction TEST = StepActivationFunction.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0f, TEST.forward(0f), 0f);
        Assertions.assertEquals(1f, TEST.forward(1f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-1f), 0f);
        Assertions.assertEquals(1f, TEST.forward(2f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-2f), 0f);
        Assertions.assertEquals(1f, TEST.forward(10f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-10f), 0f);
        Assertions.assertEquals(1f, TEST.forward(16f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-16f), 0f);
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
        Assertions.assertEquals(0f, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals("Step", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableSupport.serializeObject(TEST);
            StepActivationFunction result = SerializableSupport.deserializeObject(activationFunction);

            Assertions.assertSame(TEST, result);
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }
}
