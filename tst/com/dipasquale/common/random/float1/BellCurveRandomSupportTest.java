package com.dipasquale.common.random.float1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class BellCurveRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final UniformRandomSupport UNIFORM_RANDOM_SUPPORT = new UniformRandomSupport();
    private static final BellCurveRandomSupport TEST = new BellCurveRandomSupport(UNIFORM_RANDOM_SUPPORT, 5);

    private static void assertMultiIterationsTest(final RandomSupport randomSupport, final float minimum, final float maximum) {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(randomSupport, ITERATIONS, minimum, maximum));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(randomSupport, ITERATIONS, 0, 10, List.of(0.12f, 0.05f, 0.03f)));
    }

    @Test
    public void GIVEN_a_bell_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST, 0f, 1f);
    }

    @Test
    public void GIVEN_a_bounded_bell_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST.bounded(0.2f, 0.8f), 0.2f, 0.8f);
    }

    @Test
    public void GIVEN_a_deterministic_bell_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable() {
        BellCurveRandomSupport test = new BellCurveRandomSupport(new DeterministicRandomSupport(25), 5);

        Assertions.assertEquals(0.08333333f, test.next());
        Assertions.assertEquals(0.29166666f, test.next());
        Assertions.assertEquals(0.49999994f, test.next());
        Assertions.assertEquals(0.70833325f, test.next());
        Assertions.assertEquals(0.9166666f, test.next());
    }
}
