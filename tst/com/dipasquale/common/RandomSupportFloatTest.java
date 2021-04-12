package com.dipasquale.common;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class RandomSupportFloatTest {
    private static final FloatValue RANDOM_SEED = new FloatValue();
    private static final RandomSupportFloat TEST = () -> RANDOM_SEED.value;
    private static final int RANDOM_TEST_COUNT = 1_000_000;

    @BeforeEach
    public void beforeEach() {
        RANDOM_SEED.value = 0f;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_THEN_get_the_number() {
        Assertions.assertEquals(RANDOM_SEED.value, TEST.next(), 0f);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assertions.assertEquals(0f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.2f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_SEED.value = 0.25f;
        Assertions.assertEquals(0.25f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.35000002f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_SEED.value = 0.5f;
        Assertions.assertEquals(0.5f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_SEED.value = 0.75f;
        Assertions.assertEquals(0.75f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.65000004f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5D, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_SEED.value = 0.99f;
        Assertions.assertEquals(0.99f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.794f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assertions.assertEquals(0, TEST.next(0, 5));
        RANDOM_SEED.value = 0.19f;
        Assertions.assertEquals(0, TEST.next(0, 5));
        RANDOM_SEED.value = 0.2f;
        Assertions.assertEquals(1, TEST.next(0, 5));
        RANDOM_SEED.value = 0.39f;
        Assertions.assertEquals(1, TEST.next(0, 5));
        RANDOM_SEED.value = 0.4f;
        Assertions.assertEquals(2, TEST.next(0, 5));
        RANDOM_SEED.value = 0.59f;
        Assertions.assertEquals(2, TEST.next(0, 5));
        RANDOM_SEED.value = 0.6f;
        Assertions.assertEquals(3, TEST.next(0, 5));
        RANDOM_SEED.value = 0.79f;
        Assertions.assertEquals(3, TEST.next(0, 5));
        RANDOM_SEED.value = 0.8f;
        Assertions.assertEquals(4, TEST.next(0, 5));
        RANDOM_SEED.value = 0.99f;
        Assertions.assertEquals(4, TEST.next(0, 5));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_the_same_minimum_and_maximum_THEN_get_the_minimum_number() {
        Assertions.assertEquals(1, TEST.next(1, 1));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_random_number_generator_bounded_by_an_arbitrary_limit_THEN_use_the_random_generator_instance_to_get_the_next_number_bounded_by_the_arbitrary_limit() {
        RandomSupportFloat test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertEquals(0.2f, test.next(), 0);
        Assertions.assertEquals(0.38f, test.next(0.3f, 0.7f), 0);
        RANDOM_SEED.value = 0.5f;
        Assertions.assertEquals(0.5f, test.next(), 0);
        Assertions.assertEquals(0.5f, test.next(0.3f, 0.7f), 0);
        RANDOM_SEED.value = 0.99f;
        Assertions.assertEquals(0.794f, test.next(), 0);
        Assertions.assertEquals(0.55880004f, test.next(0.4f, 0.6f), 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0f, 0.25f));
        RANDOM_SEED.value = 0.25f;
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertFalse(TEST.isLessThan(0f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        RANDOM_SEED.value = 0.5f;
        Assertions.assertFalse(TEST.isLessThan(0.5f));
    }

    private static boolean isNextIntegerEquallyDistributed(final RandomSupportFloat randomSupport) {
        int max = 10;
        Map<Integer, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            int result = randomSupport.next(0, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (max != distribution.size()) {
            return false;
        }

        for (int i1 = 0; i1 < max; i1++) {
            for (int i2 = i1 + 1; i2 < max; i2++) {
                float ratio = (float) distribution.get(i1).get() / (float) distribution.get(i2).get();

                if (Float.compare(ratio, 0.9f) < 0 || Float.compare(ratio, 1.1f) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.create(false);

        Assertions.assertTrue(isNextIntegerEquallyDistributed(test));
    }

    private static boolean isNextFloatBounded(final RandomSupportFloat randomSupport) {
        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            float result = randomSupport.next();

            if (Float.compare(result, 0f) < 0 || Float.compare(result, 1f) >= 0) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.create(false);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.create(true);

        Assertions.assertTrue(isNextIntegerEquallyDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.create(true);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    private static boolean isNextIntegerMeanDistributed(final RandomSupportFloat randomSupport, final List<Float> marginOfErrors) {
        int max = 10;
        Map<Integer, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            int result = randomSupport.next(0, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (distribution.size() > max) {
            return false;
        }

        for (int i = 0, c = max / 2; i < c; i++) { // TODO: not checking if the ratios are increase towards the mean
            float number1 = (float) Optional.ofNullable(distribution.get(i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float number2 = (float) Optional.ofNullable(distribution.get(max - 1 - i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float ratio = number1 / number2;

            float marginOfError = i < marginOfErrors.size()
                    ? marginOfErrors.get(i)
                    : marginOfErrors.get(marginOfErrors.size() - 1);

            if (Float.compare(ratio, 1f - marginOfError) < 0 || Float.compare(ratio, 1f + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(false);

        Assertions.assertTrue(isNextIntegerMeanDistributed(test, ImmutableList.of(0.3f, 0.1f)));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(false);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(false, 12);

        Assertions.assertTrue(isNextIntegerMeanDistributed(test, ImmutableList.of(1f, 0.65f, 0.3f, 0.1f)));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(false, 12);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_gaussian_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(true);

        Assertions.assertTrue(isNextIntegerMeanDistributed(test, ImmutableList.of(0.3f, 0.1f)));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(true);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_gaussian_distributed_through_out() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(true, 12);

        Assertions.assertTrue(isNextIntegerMeanDistributed(test, ImmutableList.of(1f, 0.65f, 0.3f, 0.1f)));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupportFloat test = RandomSupportFloat.createMeanDistribution(true, 12);

        Assertions.assertTrue(isNextFloatBounded(test));
    }

    private static final class FloatValue {
        private float value;
    }
}
