package com.dipasquale.common;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class RandomSupportTest {
    private static final AtomicDouble RANDOM_SEED = new AtomicDouble();
    private static final RandomSupport TEST = RANDOM_SEED::get;
    private static final int RANDOM_TEST_COUNT = 1_000_000;

    @Before
    public void before() {
        RANDOM_SEED.set(0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_THEN_get_the_number() {
        Assert.assertEquals(RANDOM_SEED.get(), TEST.next(), 0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assert.assertEquals(0D, TEST.next(0D, 1D), 0D);
        Assert.assertEquals(0.2D, TEST.next(0.2D, 0.8D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.25D);
        Assert.assertEquals(0.25D, TEST.next(0D, 1D), 0D);
        Assert.assertEquals(0.35000000000000003D, TEST.next(0.2D, 0.8D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.5D);
        Assert.assertEquals(0.5D, TEST.next(0D, 1D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.2D, 0.8D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.75D);
        Assert.assertEquals(0.75D, TEST.next(0D, 1D), 0D);
        Assert.assertEquals(0.6500000000000001D, TEST.next(0.2D, 0.8D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
        RANDOM_SEED.set(0.99D);
        Assert.assertEquals(0.99D, TEST.next(0D, 1D), 0D);
        Assert.assertEquals(0.794D, TEST.next(0.2D, 0.8D), 0D);
        Assert.assertEquals(0.5D, TEST.next(0.5D, 0.5D), 0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_an_arbitrary_limit_THEN_get_the_number_within_the_limits() {
        Assert.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.19D);
        Assert.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.2D);
        Assert.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.39D);
        Assert.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.4D);
        Assert.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.59D);
        Assert.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.6D);
        Assert.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.79D);
        Assert.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.8D);
        Assert.assertEquals(4L, TEST.next(0L, 5L));
        RANDOM_SEED.set(0.99D);
        Assert.assertEquals(4L, TEST.next(0L, 5L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_getting_a_random_whole_number_bounded_by_the_same_minimum_and_maximum_THEN_get_the_minimum_number() {
        Assert.assertEquals(1L, TEST.next(1L, 1L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_random_number_generator_bounded_by_an_arbitrary_limit_THEN_use_the_random_generator_instance_to_get_the_next_number_bounded_by_the_arbitrary_limit() {
        RandomSupport test = TEST.bounded(0.2D, 0.8D);

        Assert.assertEquals(0.2D, test.next(), 0);
        Assert.assertEquals(0.38D, test.next(0.3D, 0.7D), 0);
        RANDOM_SEED.set(0.5D);
        Assert.assertEquals(0.5D, test.next(), 0);
        Assert.assertEquals(0.5D, test.next(0.3D, 0.7D), 0);
        RANDOM_SEED.set(0.99D);
        Assert.assertEquals(0.794D, test.next(), 0);
        Assert.assertEquals(0.5588D, test.next(0.4D, 0.6D), 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assert.assertTrue(TEST.isBetween(0D, Double.MIN_VALUE));
        Assert.assertTrue(TEST.isBetween(0D, 0.25D));
        RANDOM_SEED.set(0.25D);
        Assert.assertFalse(TEST.isBetween(0D, 0.25D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assert.assertFalse(TEST.isLessThan(0D));
        Assert.assertTrue(TEST.isLessThan(0.5D));
        RANDOM_SEED.set(0.5D);
        Assert.assertFalse(TEST.isLessThan(0.5D));
    }

    private static boolean isNextLongIsEquallyDistributed(final RandomSupport randomSupport) {
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
        RandomSupport test = RandomSupport.create();

        Assert.assertTrue(isNextLongIsEquallyDistributed(test));
    }

    private static boolean isNextDoubleIsBounded(final RandomSupport randomSupport) {
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
        RandomSupport test = RandomSupport.create();

        Assert.assertTrue(isNextDoubleIsBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupport test = RandomSupport.createConcurrent();

        Assert.assertTrue(isNextLongIsEquallyDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = RandomSupport.createConcurrent();

        Assert.assertTrue(isNextDoubleIsBounded(test));
    }

    private static boolean isNextLongIsGaussianDistributed(final RandomSupport randomSupport) {
        long max = 10L;
        Map<Long, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < RANDOM_TEST_COUNT; i++) {
            long result = randomSupport.next(0L, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (max != distribution.size()) {
            return false;
        }

        List<Double> marginOfErrors = ImmutableList.<Double>builder()
                .add(0.5D)
                .add(0.25D)
                .add(0.1D)
                .add(0.1D)
                .add(0.1D)
                .build();

        for (long i = 0L, c = max / 2; i < c; i++) {
            double ratio = (double) distribution.get(i).get() / (double) distribution.get(max - 1L - i).get();
            double marginOfError = marginOfErrors.get((int) i);

            if (Double.compare(ratio, 1D - marginOfError) < 0 || Double.compare(ratio, 1D + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_equally_distributed_through_out() {
        RandomSupport test = RandomSupport.createGaussian();

        Assert.assertTrue(isNextLongIsGaussianDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = RandomSupport.createGaussian();

        Assert.assertTrue(isNextDoubleIsBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_gaussian_distributed_through_out() {
        RandomSupport test = RandomSupport.createGaussianConcurrent();

        Assert.assertTrue(isNextLongIsGaussianDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = RandomSupport.createGaussianConcurrent();

        Assert.assertTrue(isNextDoubleIsBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_and_unbounded_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_not_gaussian_distributed_through_out() {
        RandomSupport test = RandomSupport.createGaussianUnbounded();

        Assert.assertFalse(isNextLongIsGaussianDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_and_unbounded_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_beyond_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = RandomSupport.createGaussianUnbounded();

        Assert.assertFalse(isNextDoubleIsBounded(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_and_unbounded_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_are_not_gaussian_distributed_through_out() {
        RandomSupport test = RandomSupport.createGaussianConcurrentUnbounded();

        Assert.assertFalse(isNextLongIsGaussianDistributed(test));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_gaussian_based_and_unbounded_that_is_thread_safe_WHEN_generating_multiple_random_numbers_THEN_the_numbers_range_beyond_from_0_inclusively_to_1_exclusively() {
        RandomSupport test = RandomSupport.createGaussianConcurrentUnbounded();

        Assert.assertFalse(isNextDoubleIsBounded(test));
    }
}
