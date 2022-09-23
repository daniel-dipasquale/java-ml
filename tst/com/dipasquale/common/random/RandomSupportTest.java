package com.dipasquale.common.random;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class RandomSupportTest {
    private static final float MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final double MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private static final RandomSupportMock TEST = new RandomSupportMock();

    @BeforeEach
    public void beforeEach() {
        TEST.nextFloat.value = 0f;
        TEST.nextDouble.value = 0D;
    }

    @Test
    public void GIVEN_a_random_float_number_generator_WHEN_generates_a_sometimes_bounded_but_always_random_number_THEN_get_a_random_float_number() {
        Assertions.assertEquals(0f, TEST.nextFloat());
        Assertions.assertEquals(0f, TEST.nextFloat(0f, 1f));
        Assertions.assertEquals(0.2f, TEST.nextFloat(0.2f, 0.8f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.5f, 0.5f));
        TEST.nextFloat.value = 0.25f;
        Assertions.assertEquals(0.25f, TEST.nextFloat());
        Assertions.assertEquals(0.25f, TEST.nextFloat(0f, 1f));
        Assertions.assertEquals(0.35000002f, TEST.nextFloat(0.2f, 0.8f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.5f, 0.5f));
        TEST.nextFloat.value = 0.5f;
        Assertions.assertEquals(0.5f, TEST.nextFloat());
        Assertions.assertEquals(0.5f, TEST.nextFloat(0f, 1f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.2f, 0.8f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.5f, 0.5f));
        TEST.nextFloat.value = 0.75f;
        Assertions.assertEquals(0.75f, TEST.nextFloat());
        Assertions.assertEquals(0.75f, TEST.nextFloat(0f, 1f));
        Assertions.assertEquals(0.65000004f, TEST.nextFloat(0.2f, 0.8f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.5f, 0.5f));
        TEST.nextFloat.value = MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE, TEST.nextFloat());
        Assertions.assertEquals(MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE, TEST.nextFloat(0f, 1f));
        Assertions.assertEquals(0.79999995f, TEST.nextFloat(0.2f, 0.8f));
        Assertions.assertEquals(0.5f, TEST.nextFloat(0.5f, 0.5f));
    }

    @Test
    public void GIVEN_a_random_double_number_generator_WHEN_generates_a_sometimes_bounded_but_always_random_number_THEN_get_a_random_double_number() {
        Assertions.assertEquals(0D, TEST.nextDouble());
        Assertions.assertEquals(0D, TEST.nextDouble(0D, 1D));
        Assertions.assertEquals(0.2D, TEST.nextDouble(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.5D, 0.5D));
        TEST.nextDouble.value = 0.25D;
        Assertions.assertEquals(0.25D, TEST.nextDouble());
        Assertions.assertEquals(0.25D, TEST.nextDouble(0D, 1D));
        Assertions.assertEquals(0.35000000000000003D, TEST.nextDouble(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.5D, 0.5D));
        TEST.nextDouble.value = 0.5D;
        Assertions.assertEquals(0.5D, TEST.nextDouble());
        Assertions.assertEquals(0.5D, TEST.nextDouble(0D, 1D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.5D, 0.5D));
        TEST.nextDouble.value = 0.75D;
        Assertions.assertEquals(0.75D, TEST.nextDouble());
        Assertions.assertEquals(0.75D, TEST.nextDouble(0D, 1D));
        Assertions.assertEquals(0.6500000000000001D, TEST.nextDouble(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.5D, 0.5D));
        TEST.nextDouble.value = MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE, TEST.nextDouble());
        Assertions.assertEquals(MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE, TEST.nextDouble(0D, 1D));
        Assertions.assertEquals(0.8D, TEST.nextDouble(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.nextDouble(0.5D, 0.5D));
    }

    @Test
    public void GIVEN_a_random_float_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_float_number() {
        Assertions.assertEquals(0, TEST.nextInteger(0, 1));
        Assertions.assertEquals(0, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.19f;
        Assertions.assertEquals(0, TEST.nextInteger(0, 1));
        Assertions.assertEquals(0, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.2f;
        Assertions.assertEquals(1, TEST.nextInteger(1, 2));
        Assertions.assertEquals(1, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.39f;
        Assertions.assertEquals(1, TEST.nextInteger(1, 2));
        Assertions.assertEquals(1, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.4f;
        Assertions.assertEquals(2, TEST.nextInteger(2, 3));
        Assertions.assertEquals(2, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.59f;
        Assertions.assertEquals(2, TEST.nextInteger(2, 3));
        Assertions.assertEquals(2, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.6f;
        Assertions.assertEquals(3, TEST.nextInteger(3, 4));
        Assertions.assertEquals(3, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.79f;
        Assertions.assertEquals(3, TEST.nextInteger(3, 4));
        Assertions.assertEquals(3, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.8f;
        Assertions.assertEquals(4, TEST.nextInteger(4, 5));
        Assertions.assertEquals(4, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = 0.99f;
        Assertions.assertEquals(4, TEST.nextInteger(4, 5));
        Assertions.assertEquals(4, TEST.nextInteger(0, 5));
        TEST.nextFloat.value = MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(4, TEST.nextInteger(4, 5));
        Assertions.assertEquals(4, TEST.nextInteger(0, 5));
    }

    @Test
    public void GIVEN_a_random_double_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_double_number() {
        Assertions.assertEquals(0L, TEST.nextLong(0L, 1L));
        Assertions.assertEquals(0L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.19D;
        Assertions.assertEquals(0L, TEST.nextLong(0L, 1L));
        Assertions.assertEquals(0L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.2D;
        Assertions.assertEquals(1L, TEST.nextLong(1L, 2L));
        Assertions.assertEquals(1L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.39D;
        Assertions.assertEquals(1L, TEST.nextLong(1L, 2L));
        Assertions.assertEquals(1L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.4D;
        Assertions.assertEquals(2L, TEST.nextLong(2L, 3L));
        Assertions.assertEquals(2L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.59D;
        Assertions.assertEquals(2L, TEST.nextLong(2L, 3L));
        Assertions.assertEquals(2L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.6D;
        Assertions.assertEquals(3L, TEST.nextLong(3L, 4L));
        Assertions.assertEquals(3L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.79D;
        Assertions.assertEquals(3L, TEST.nextLong(3L, 4L));
        Assertions.assertEquals(3L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.8D;
        Assertions.assertEquals(4L, TEST.nextLong(4L, 5L));
        Assertions.assertEquals(4L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = 0.99D;
        Assertions.assertEquals(4L, TEST.nextLong(4L, 5L));
        Assertions.assertEquals(4L, TEST.nextLong(0L, 5L));
        TEST.nextDouble.value = MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(4L, TEST.nextLong(4L, 5L));
        Assertions.assertEquals(4L, TEST.nextLong(0L, 5L));
    }

    @Test
    public void GIVEN_a_random_float_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        TEST.nextFloat.value = 0.19f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        TEST.nextFloat.value = 0.39f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertTrue(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        TEST.nextFloat.value = 0.59f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertTrue(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        TEST.nextFloat.value = 0.79f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertTrue(TEST.isBetween(0.75f, 1f));
        TEST.nextFloat.value = MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertTrue(TEST.isBetween(0.75f, 1f));
    }

    @Test
    public void GIVEN_a_random_double_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        TEST.nextDouble.value = 0.19D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        TEST.nextDouble.value = 0.39D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertTrue(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        TEST.nextDouble.value = 0.59D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertTrue(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        TEST.nextDouble.value = 0.79D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertTrue(TEST.isBetween(0.75D, 1D));
        TEST.nextDouble.value = MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertTrue(TEST.isBetween(0.75D, 1D));
    }

    @Test
    public void GIVEN_a_random_float_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        TEST.nextFloat.value = 0.19f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        TEST.nextFloat.value = 0.39f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        TEST.nextFloat.value = 0.59f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        TEST.nextFloat.value = 0.79f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertFalse(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        TEST.nextFloat.value = MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertFalse(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
    }

    @Test
    public void GIVEN_a_random_double_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        TEST.nextDouble.value = 0.19D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        TEST.nextDouble.value = 0.39D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        TEST.nextDouble.value = 0.59D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        TEST.nextDouble.value = 0.79D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertFalse(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        TEST.nextDouble.value = MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertFalse(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_shuffling_elements_in_place_THEN_shuffle_all_elements() {
        DeterministicRandomSupport test = DeterministicRandomSupport.create(2L);
        int size = 10;

        List<Integer> elements = new ArrayList<>(IntStream.range(0, size)
                .mapToObj(index -> size - 1 - index)
                .toList());

        Assertions.assertEquals(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0), elements);
        test.shuffle(elements);
        Assertions.assertEquals(List.of(8, 6, 7, 4, 5, 2, 3, 0, 1, 9), elements);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_copy_of_the_shuffled_elements_THEN_create_a_copy_of_the_shuffled_elements() {
        DeterministicRandomSupport test = DeterministicRandomSupport.create(2L);
        int size = 10;

        List<Integer> elements = new ArrayList<>(IntStream.range(0, size)
                .mapToObj(index -> size - 1 - index)
                .toList());

        Assertions.assertEquals(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0), elements);
        Assertions.assertEquals(List.of(8, 6, 7, 4, 5, 2, 3, 0, 1, 9), test.createShuffled(elements, Integer.class));
        Assertions.assertEquals(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0), elements);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NextFloatMock {
        private float value = 0f;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NextDoubleMock {
        private double value = 0D;
    }

    private static final class RandomSupportMock implements RandomSupport {
        private final NextFloatMock nextFloat = new NextFloatMock();
        private final NextDoubleMock nextDouble = new NextDoubleMock();

        @Override
        public float nextFloat() {
            return nextFloat.value;
        }

        @Override
        public double nextDouble() {
            return nextDouble.value;
        }
    }
}
