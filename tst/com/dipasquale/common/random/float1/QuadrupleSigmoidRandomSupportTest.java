package com.dipasquale.common.random.float1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class QuadrupleSigmoidRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final UniformRandomSupport UNIFORM_RANDOM_SUPPORT = new UniformRandomSupport();
    private static final QuadrupleSigmoidRandomSupport TEST = new QuadrupleSigmoidRandomSupport(UNIFORM_RANDOM_SUPPORT, 0.99f, 0.05f, (float) Math.pow(10D, 3D));

    private static void assertMultiIterationsTest(final RandomSupport randomSupport, final float min, final float max) {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(randomSupport, ITERATIONS, min, max));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(randomSupport, ITERATIONS, 0, 10, List.of(0.03f)));
    }

    @Test
    public void GIVEN_a_quadruple_sigmoid_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST, 0f, 1f);
    }

    @Test
    public void GIVEN_a_bounded_quadruple_sigmoid_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST.bounded(0.2f, 0.8f), 0.2f, 0.8f);
    }

    @Test
    public void GIVEN_a_deterministic_quadruple_sigmoid_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable() {
        QuadrupleSigmoidRandomSupport test = new QuadrupleSigmoidRandomSupport(new DeterministicRandomSupport(5), 0.75f, 0.25f, (float) Math.pow(10D, 2D));

        Assertions.assertEquals(0f, test.next());
        Assertions.assertEquals(0.46415645f, test.next());
        Assertions.assertEquals(0.49999997f, test.next());
        Assertions.assertEquals(0.53584343f, test.next());
        Assertions.assertEquals(0.99999994f, test.next());
    }
}
