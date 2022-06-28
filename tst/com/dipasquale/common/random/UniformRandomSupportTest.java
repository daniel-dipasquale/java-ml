package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class UniformRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final float MINIMUM = 0f;
    private static final float MAXIMUM = 1f;
    private static final UniformRandomSupport TEST = new UniformRandomSupport();

    @Test
    public void GIVEN_a_uniform_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, MINIMUM, MAXIMUM));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(TEST, ITERATIONS, 0, 10, List.of(0.01f)));
    }

    @Test
    public void GIVEN_a_uniform_random_support_with_a_hardcoded_seed_WHEN_generating_random_numbers_THEN_generate_the_same_sequence() {
        UniformRandomSupport test = new UniformRandomSupport(0L);

        Assertions.assertEquals(0.73096776f, test.nextFloat());
        Assertions.assertEquals(0.831441f, test.nextFloat());
        Assertions.assertEquals(0.24053639f, test.nextFloat());
        Assertions.assertEquals(0.6063452f, test.nextFloat());
        Assertions.assertEquals(0.6374174f, test.nextFloat());
    }
}
