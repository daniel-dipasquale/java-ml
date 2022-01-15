package com.dipasquale.common.random.float1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class ThreadLocalUniformRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final ThreadLocalUniformRandomSupport TEST = ThreadLocalUniformRandomSupport.getInstance();

    private static void assertMultiIterationsTest(final RandomSupport randomSupport, final float min, final float max) {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(randomSupport, ITERATIONS, min, max));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(randomSupport, ITERATIONS, 0, 10, List.of(0.01f)));
    }

    @Test
    public void GIVEN_a_thread_local_uniform_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST, 0f, 1f);
    }

    @Test
    public void GIVEN_a_bounded_thread_local_uniform_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST.bounded(0.2f, 0.8f), 0.2f, 0.8f);
    }
}
