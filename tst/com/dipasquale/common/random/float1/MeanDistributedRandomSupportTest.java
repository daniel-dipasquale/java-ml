package com.dipasquale.common.random.float1;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class MeanDistributedRandomSupportTest {
    private static final int ITERATIONS = 1_000_000;
    private static final DefaultRandomSupport DEFAULT_RANDOM_SUPPORT = new DefaultRandomSupport();
    private static final MeanDistributedRandomSupport TEST = new MeanDistributedRandomSupport(DEFAULT_RANDOM_SUPPORT, 5);

    @Test
    public void GIVEN_a_mean_distributed_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(TEST, ITERATIONS, 0f, 1f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(TEST, ITERATIONS, 0, 10, ImmutableList.of(0.2f, 0.12f, 0.09f, 0.01f)));
    }

    @Test
    public void GIVEN_a_bounded_mean_distributed_random_support_WHEN_generating_random_numbers_THEN_expect_the_generation_to_be_bounded_and_evenly_distributed() {
        RandomSupport test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertTrue(RandomSupportTestSupport.isNextFloatBounded(test, ITERATIONS, 0.2f, 0.8f));
        Assertions.assertTrue(RandomSupportTestSupport.isNextIntegerMeanDistributed(test, ITERATIONS, 0, 10, ImmutableList.of(0.2f, 0.12f, 0.09f, 0.01f)));
    }
}
