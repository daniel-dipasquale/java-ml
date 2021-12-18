package com.dipasquale.common.random.float1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class DeterministicRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final int SIZE = 11;
    private static DeterministicRandomSupport TEST;

    @BeforeEach
    public void beforeEach() {
        TEST = new DeterministicRandomSupport(SIZE);
    }

    private static void assertMultiIterationsTest(final RandomSupport randomSupport, final float min, final float max) {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(randomSupport, ITERATIONS, min, max));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(randomSupport, ITERATIONS, 0, SIZE, List.of(0.001f)));
    }

    @Test
    public void GIVEN_a_deterministic_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST, 0f, 1f);
    }

    @Test
    public void GIVEN_a_bounded_deterministic_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        assertMultiIterationsTest(TEST.bounded(0.2f, 0.8f), 0.2f, 0.8f);
    }

    @Test
    public void GIVEN_a_deterministic_random_support_WHEN_generates_a_random_number_THEN_expect_the_generation_to_be_predictable() {
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(0f, TEST.next(), 0f);
            Assertions.assertEquals(0.099999994f, TEST.next(), 0f);
            Assertions.assertEquals(0.19999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.29999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.39999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.49999997f, TEST.next(), 0f);
            Assertions.assertEquals(0.59999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.6999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.79999995f, TEST.next(), 0f);
            Assertions.assertEquals(0.8999999f, TEST.next(), 0f);
            Assertions.assertEquals(0.99999994f, TEST.next(), 0f);
        }
    }
}
