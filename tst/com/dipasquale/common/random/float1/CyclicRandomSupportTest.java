package com.dipasquale.common.random.float1;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CyclicRandomSupportTest {
    private static final int ITERATIONS = 1_000_000;
    private static final int SIZE = 10;
    private static CyclicRandomSupport TEST;

    @BeforeEach
    public void beforeEach() {
        TEST = new CyclicRandomSupport(SIZE);
    }

    @Test
    public void GIVEN_a_constant_random_support_WHEN_generates_a_random_number_THEN_get_a_predictable_number() {
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(0f, TEST.next(), 0f);
            Assertions.assertEquals(0.1f, TEST.next(), 0f);
            Assertions.assertEquals(0.2f, TEST.next(), 0f);
            Assertions.assertEquals(0.3f, TEST.next(), 0f);
            Assertions.assertEquals(0.4f, TEST.next(), 0f);
            Assertions.assertEquals(0.5f, TEST.next(), 0f);
            Assertions.assertEquals(0.6f, TEST.next(), 0f);
            Assertions.assertEquals(0.7f, TEST.next(), 0f);
            Assertions.assertEquals(0.8f, TEST.next(), 0f);
            Assertions.assertEquals(0.9f, TEST.next(), 0f);
            Assertions.assertEquals(0.99999994f, TEST.next(), 0f);
        }
    }

    @Test
    public void GIVEN_a_constant_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, 0f, 1f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(TEST, ITERATIONS, 0, SIZE + 1, ImmutableList.of(0.25f, 0.15f, 0.12f, 0.03f)));
    }

    @Test
    public void GIVEN_a_bounded_constant_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        RandomSupport test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(test, ITERATIONS, 0.2f, 0.8f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(TEST, ITERATIONS, 0, SIZE + 1, ImmutableList.of(0.25f, 0.15f, 0.12f, 0.03f)));
    }
}
