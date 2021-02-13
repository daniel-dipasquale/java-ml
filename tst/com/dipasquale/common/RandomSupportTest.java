package com.dipasquale.common;

import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class RandomSupportTest {
    private static final AtomicDouble RANDOM_SEED = new AtomicDouble();
    private static final RandomSupport TEST = RANDOM_SEED::get;

    @Before
    public void before() {
        RANDOM_SEED.set(0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_a_random_number_THEN_generate_a_random_number() {
        Assert.assertEquals(RANDOM_SEED.get(), TEST.next(), 0D);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generating_a_bounded_random_number_THEN_generate_a_bounded_random_number() {
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
    public void GIVEN_a_random_number_generator_WHEN_generating_a_bounded_random_whole_number_THEN_generate_a_bounded_random_whole_number_by_assigning_the_domain_of_numbers_an_equal_distribution() {
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
    public void GIVEN_a_random_number_generator_WHEN_generating_a_bounded_random_whole_number_with_a_domain_of_zero_numbers_THEN_short_circuit_the_generation_and_provide_the_min_and_max_as_the_generated_number() {
        Assert.assertEquals(1L, TEST.next(1L, 1L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_permanent_bounded_random_number_generator_THEN_use_the_random_number_generator_permanently_bound_to_the_range_it_was_created_with() {
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
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assert.assertTrue(TEST.isBetween(0D, 0D));
        Assert.assertTrue(TEST.isBetween(0D, 0.25D));
        RANDOM_SEED.set(0.26D);
        Assert.assertFalse(TEST.isBetween(0D, 0.25D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assert.assertTrue(TEST.isAtMost(0D));
        Assert.assertTrue(TEST.isAtMost(0.5D));
        RANDOM_SEED.set(0.51D);
        Assert.assertFalse(TEST.isAtMost(0.5D));
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_unsafe_WHEN_generating_a_random_number_THEN_generate_the_random_number_between_0_and_1() {
        RandomSupport test = RandomSupport.create();
        double result = test.next();

        Assert.assertTrue(Double.compare(result, 0D) >= 0);
        Assert.assertTrue(Double.compare(result, 1D) <= 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_WHEN_generating_a_random_number_THEN_generate_the_random_number_between_0_and_1() {
        RandomSupport test = RandomSupport.createConcurrent();
        double result = test.next();

        Assert.assertTrue(Double.compare(result, 0D) >= 0);
        Assert.assertTrue(Double.compare(result, 1D) <= 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_unsafe_and_tends_to_generate_random_numbers_based_on_a_bell_curve_distribution_WHEN_generating_a_random_number_THEN_generate_the_random_number_between_0_and_1() {
        RandomSupport test = RandomSupport.createGaussian();
        double result = test.next();

        Assert.assertTrue(Double.compare(result, 0D) >= 0);
        Assert.assertTrue(Double.compare(result, 1D) <= 0);
    }

    @Test
    public void GIVEN_a_random_number_generator_that_is_thread_safe_and_tends_to_generate_random_numbers_based_on_a_bell_curve_distribution_WHEN_generating_a_random_number_THEN_generate_the_random_number_between_0_and_1() {
        RandomSupport test = RandomSupport.createGaussianConcurrent();
        double result = test.next();

        Assert.assertTrue(Double.compare(result, 0D) >= 0);
        Assert.assertTrue(Double.compare(result, 1D) <= 0);
    }
}
