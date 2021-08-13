/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.function.activation.function;

import com.dipasquale.ai.common.function.activation.ReLUActivationFunction;
import com.dipasquale.common.test.SerializableSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ReLUActivationFunctionTest {
    private static final ReLUActivationFunction TEST = ReLUActivationFunction.getInstance();

    @Test
    public void TEST_1() {
        Assertions.assertEquals(0f, TEST.forward(0f), 0f);
        Assertions.assertEquals(1f, TEST.forward(1f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-1f), 0f);
        Assertions.assertEquals(2f, TEST.forward(2f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-2f), 0f);
        Assertions.assertEquals(10f, TEST.forward(10f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-10f), 0f);
        Assertions.assertEquals(16f, TEST.forward(16f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-16f), 0f);
        Assertions.assertEquals(50f, TEST.forward(50f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-50f), 0f);
        Assertions.assertEquals(75f, TEST.forward(75f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-75f), 0f);
        Assertions.assertEquals(89f, TEST.forward(89f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-89f), 0f);
        Assertions.assertEquals(90f, TEST.forward(90f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-90f), 0f);
        Assertions.assertEquals(100f, TEST.forward(100f), 0f);
        Assertions.assertEquals(0f, TEST.forward(-100f), 0f);
        Assertions.assertEquals(Float.MAX_VALUE, TEST.forward(Float.MAX_VALUE), 0f);
        Assertions.assertEquals(0f, TEST.forward(-Float.MAX_VALUE), 0f);
        Assertions.assertEquals(Float.POSITIVE_INFINITY, TEST.forward(Float.POSITIVE_INFINITY), 0f);
        Assertions.assertEquals(0f, TEST.forward(Float.NEGATIVE_INFINITY), 0f);
        Assertions.assertEquals(Float.NaN, TEST.forward(Float.NaN), 0f);
    }

    @Test
    public void TEST_2() {
        Assertions.assertEquals("ReLU", TEST.toString());
    }

    @Test
    public void TEST_3() {
        try {
            byte[] activationFunction = SerializableSupport.serialize(TEST);
            ReLUActivationFunction result = SerializableSupport.deserialize(activationFunction);

            Assertions.assertSame(TEST, result);
        } catch (Throwable e) {
            Assertions.fail(e.getMessage());
        }
    }
}
