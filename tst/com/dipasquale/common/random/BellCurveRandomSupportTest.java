package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class BellCurveRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final float MINIMUM = 0f;
    private static final float MAXIMUM = 1f;
    private static final UniformRandomSupport UNIFORM_RANDOM_SUPPORT = new UniformRandomSupport();
    private static final BellCurveRandomSupport TEST = new BellCurveRandomSupport(UNIFORM_RANDOM_SUPPORT, 5);

    @Test
    public void GIVEN_a_bell_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, MINIMUM, MAXIMUM));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(TEST, ITERATIONS, 0, 10, List.of(0.12f, 0.05f, 0.03f)));
    }

    @Test
    public void GIVEN_a_deterministic_bell_curve_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_predictable() {
        BellCurveRandomSupport test = new BellCurveRandomSupport(DeterministicRandomSupport.create(25L), 5);

        Assertions.assertEquals(0.08333333f, test.nextFloat());
        Assertions.assertEquals(0.29166666f, test.nextFloat());
        Assertions.assertEquals(0.49999994f, test.nextFloat());
        Assertions.assertEquals(0.70833325f, test.nextFloat());
        Assertions.assertEquals(0.9166666f, test.nextFloat());
    }
}
