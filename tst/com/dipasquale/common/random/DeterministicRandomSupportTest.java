package com.dipasquale.common.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class DeterministicRandomSupportTest {
    private static final int ITERATIONS = 10_000_000;
    private static final float MINIMUM = 0f;
    private static final float MAXIMUM = 1f;
    private static final int SIZE = 11;
    private static DeterministicRandomSupport TEST;

    @BeforeEach
    public void beforeEach() {
        TEST = DeterministicRandomSupport.create(SIZE);
    }

    @Test
    public void GIVEN_a_deterministic_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, MINIMUM, MAXIMUM));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerEvenlyDistributed(TEST, ITERATIONS, 0, SIZE, List.of(0.001f)));
    }

    @Test
    public void GIVEN_a_deterministic_random_support_WHEN_generates_a_random_number_THEN_expect_the_generation_to_be_predictable() {
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(0f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.099999994f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.19999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.29999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.39999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.49999997f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.59999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.6999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.79999995f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.8999999f, TEST.nextFloat(), 0f);
            Assertions.assertEquals(0.99999994f, TEST.nextFloat(), 0f);
        }
    }
}
