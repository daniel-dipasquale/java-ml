package com.dipasquale.common.random.float2;

import com.google.common.util.concurrent.AtomicDouble;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class RandomSupportTest {
    private static final AtomicDouble RANDOM_SEED = new AtomicDouble();
    private static final RandomSupport TEST = RANDOM_SEED::get;
    private static final int RANDOM_TEST_COUNT = 1_000_000;

    @BeforeEach
    public void beforeEach() {
        RANDOM_SEED.set(0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_THEN_get_the_number() {
        Assertions.assertEquals(RANDOM_SEED.get(), TEST.next(), 0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assertions.assertEquals(0D, TEST.next(0D, 1D), 0D);
        Assertions.assertEquals(0.2D, TEST.next(0.2D, 0.8D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.25D);
        Assertions.assertEquals(0.25D, TEST.next(0D, 1D), 0D);
        Assertions.assertEquals(0.35000000000000003D, TEST.next(0.2D, 0.8D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.5D);
        Assertions.assertEquals(0.5D, TEST.next(0D, 1D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.2D, 0.8D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.75D);
        Assertions.assertEquals(0.75D, TEST.next(0D, 1D), 0D);
        Assertions.assertEquals(0.6500000000000001D, TEST.next(0.2D, 0.8D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.99D);
        Assertions.assertEquals(0.99D, TEST.next(0D, 1D), 0D);
        Assertions.assertEquals(0.794D, TEST.next(0.2D, 0.8D), 0D);
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assertions.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.19D);
        Assertions.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.2D);
        Assertions.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.39D);
        Assertions.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.4D);
        Assertions.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.59D);
        Assertions.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.6D);
        Assertions.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.79D);
        Assertions.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.8D);
        Assertions.assertEquals(4L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.99D);
        Assertions.assertEquals(4L, TEST.next(0L, 5L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_the_same_minimum_and_maximum_THEN_get_the_minimum_number() {
        Assertions.assertEquals(1L, TEST.next(1L, 1L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_random_number_generator_bounded_by_an_arbitrary_limit_THEN_use_the_random_generator_instance_to_get_the_next_number_bounded_by_the_arbitrary_limit() {
        RandomSupport test = TEST.bounded(0.2D, 0.8D);

        Assertions.assertEquals(0.2D, test.next(), 0);
        Assertions.assertEquals(0.38D, test.next(0.3D, 0.7D), 0);
        RANDOM_SEED.set(0.5D);
        Assertions.assertEquals(0.5D, test.next(), 0);
        Assertions.assertEquals(0.5D, test.next(0.3D, 0.7D), 0);
        RANDOM_SEED.set(0.99D);
        Assertions.assertEquals(0.794D, test.next(), 0);
        Assertions.assertEquals(0.5588D, test.next(0.4D, 0.6D), 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0D, 0.25D));
        RANDOM_SEED.set(0.25D);
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertFalse(TEST.isLessThan(0D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        RANDOM_SEED.set(0.5D);
        Assertions.assertFalse(TEST.isLessThan(0.5D));
    }

    private static boolean isNextLongEquallyDistributed(final RandomSupport randomSupport) {
        long max = 10L;
        Map<Long, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            long result = randomSupport.next(0L, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (max != distribution.size()) {
            return false;
        }

        for (long i1 = 0L; i1 < max; i1++) {
            for (long i2 = i1 + 1L; i2 < max; i2++) {
                double ratio = (double) distribution.get(i1).get() / (double) distribution.get(i2).get();

                if (Double.compare(ratio, 0.9D) < 0 || Double.compare(ratio, 1.1D) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupport test = new DefaultRandomSupport();

        Assertions.assertTrue(isNextLongEquallyDistributed(test));
    }

    private static boolean isNextDoubleBounded(final RandomSupport randomSupport) {
        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            double result = randomSupport.next();

            if (Double.compare(result, 0D) < 0 || Double.compare(result, 1D) >= 0) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = new DefaultRandomSupport();

        Assertions.assertTrue(isNextDoubleBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupport test = new DefaultRandomSupport();

        Assertions.assertTrue(isNextLongEquallyDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = new DefaultRandomSupport();

        Assertions.assertTrue(isNextDoubleBounded(test));
    }

    private static boolean isNextLongMeanDistributed(final RandomSupport randomSupport, final List<Double> marginOfErrors) {
        long max = 10L;
        Map<Long, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            long result = randomSupport.next(0L, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (distribution.size() > max) {
            return false;
        }

        for (long i = 0L, c = max / 2L; i < c; i++) { // TODO: not checking if the ratios are increase towards the mean
            double number1 = (double) Optional.ofNullable(distribution.get(i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            double number2 = (double) Optional.ofNullable(distribution.get(max - 1L - i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            double ratio = number1 / number2;
            int index = (int) i;

            double marginOfError = index < marginOfErrors.size()
                    ? marginOfErrors.get(index)
                    : marginOfErrors.get(marginOfErrors.size() - 1);

            if (Double.compare(ratio, 1D - marginOfError) < 0 || Double.compare(ratio, 1D + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }

//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
//        RandomSupport test = RandomSupport.createMeanDistribution(false);
//
//        Assertions.assertTrue(isNextLongMeanDistributed(test, ImmutableList.of(0.3D, 0.1D)));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
//        RandomSupport test = RandomSupport.createMeanDistribution(false);
//
//        Assertions.assertTrue(isNextDoubleBounded(test));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
//        RandomSupport test = RandomSupport.createMeanDistribution(false, 12);
//
//        Assertions.assertTrue(isNextLongMeanDistributed(test, ImmutableList.of(1D, 0.65D, 0.3D, 0.1D)));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
//        RandomSupport test = RandomSupport.createMeanDistribution(false, 12);
//
//        Assertions.assertTrue(isNextDoubleBounded(test));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_gaussian_distributed_through_out() {
//        RandomSupport test = RandomSupport.createMeanDistribution(true);
//
//        Assertions.assertTrue(isNextLongMeanDistributed(test, ImmutableList.of(0.3D, 0.1D)));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
//        RandomSupport test = RandomSupport.createMeanDistribution(true);
//
//        Assertions.assertTrue(isNextDoubleBounded(test));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_gaussian_distributed_through_out() {
//        RandomSupport test = RandomSupport.createMeanDistribution(true, 12);
//
//        Assertions.assertTrue(isNextLongMeanDistributed(test, ImmutableList.of(1D, 0.65D, 0.3D, 0.1D)));
//    }
//
//    @Test
//    public void GIVEN_a_random_number_generator_that_is_mean_distributed_heavily_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
//        RandomSupport test = RandomSupport.createMeanDistribution(true, 12);
//
//        Assertions.assertTrue(isNextDoubleBounded(test));
//    }
}
