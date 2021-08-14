package com.dipasquale.common.random.float1;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DefaultRandomSupportTest {
    private static final int ITERATIONS = 1_000_000;
    private static final DefaultRandomSupport TEST = new DefaultRandomSupport();

    @Test
    public void GIVEN_a_default_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, 0f, 1f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(TEST, ITERATIONS, 0, 10, ImmutableList.of(0.25f, 0.15f, 0.12f, 0.03f)));
    }

    @Test
    public void GIVEN_a_bounded_default_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        RandomSupport test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(test, ITERATIONS, 0.2f, 0.8f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(TEST, ITERATIONS, 0, 10, ImmutableList.of(0.25f, 0.15f, 0.12f, 0.03f)));
    }

    @Test
    public void GIVEN_a_default_random_support_with_a_hardcoded_seed_WHEN_generating_random_numbers_THEN_generate_the_same_sequence() {
        DefaultRandomSupport test = new DefaultRandomSupport(0L);

        Assertions.assertEquals(0.73096776f, test.next());
        Assertions.assertEquals(0.831441f, test.next());
        Assertions.assertEquals(0.24053639f, test.next());
        Assertions.assertEquals(0.6063452f, test.next());
        Assertions.assertEquals(0.6374174f, test.next());
    }
}
