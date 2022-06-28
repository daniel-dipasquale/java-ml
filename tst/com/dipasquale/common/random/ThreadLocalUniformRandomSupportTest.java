package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class ThreadLocalUniformRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final float MINIMUM = 0f;
    private static final float MAXIMUM = 1f;
    private static final ThreadLocalUniformRandomSupport TEST = ThreadLocalUniformRandomSupport.getInstance();

    @Test
    public void GIVEN_a_thread_local_uniform_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, MINIMUM, MAXIMUM));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(TEST, ITERATIONS, 0, 10, List.of(0.01f)));
    }
}
