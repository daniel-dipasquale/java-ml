package com.dipasquale.ai.rl.neat.function.activation;

import com.dipasquale.io.serialization.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SigmoidActivationFunctionTest {
    private static final SigmoidActivationFunction TEST = SigmoidActivationFunction.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0.5f, TEST.forward(0f), 0f);
        Assertions.assertEquals(0.7310586f, TEST.forward(1f), 0f);
        Assertions.assertEquals(0.26894143f, TEST.forward(-1f), 0f);
        Assertions.assertEquals(0.880797f, TEST.forward(2f), 0f);
        Assertions.assertEquals(0.11920292f, TEST.forward(-2f), 0f);
        Assertions.assertEquals(0.9999546f, TEST.forward(10f), 0f);
        Assertions.assertEquals(4.5397872E-5f, TEST.forward(-10f), 0f);
        Assertions.assertEquals(0.9999999f, TEST.forward(16f), 0f);
        Assertions.assertEquals(1.12535155E-7f, TEST.forward(-16f), 0f);
        Assertions.assertEquals(1f, TEST.forward(50f), 0f);
        Assertions.assertEquals(1.9287499E-22f, TEST.forward(-50f), 0f);
        Assertions.assertEquals(1f, TEST.forward(75f), 0f);
        Assertions.assertEquals(2.678637E-33f, TEST.forward(-75f), 0f);
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
        Assertions.assertEquals("Sigmoid", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableSupport.serializeObject(TEST);
            SigmoidActivationFunction result = SerializableSupport.deserializeObject(activationFunction);

            Assertions.assertSame(TEST, result);
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }
}
