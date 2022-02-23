package com.dipasquale.common.random.float2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public final class RandomSupportTest {
    private static final double MAXIMUM_SAFE_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private static final DoubleValue RANDOM_STATE = new DoubleValue();
    private static final RandomSupport TEST = () -> RANDOM_STATE.value;

    @BeforeEach
    public void beforeEach() {
        RANDOM_STATE.value = 0D;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generates_a_sometimes_bounded_but_always_random_number_THEN_get_a_random_number() {
        Assertions.assertEquals(0D, TEST.next());
        Assertions.assertEquals(0D, TEST.next(0D, 1D));
        Assertions.assertEquals(0.2D, TEST.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.25D;
        Assertions.assertEquals(0.25D, TEST.next());
        Assertions.assertEquals(0.25D, TEST.next(0D, 1D));
        Assertions.assertEquals(0.35000000000000003D, TEST.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.5D;
        Assertions.assertEquals(0.5D, TEST.next());
        Assertions.assertEquals(0.5D, TEST.next(0D, 1D));
        Assertions.assertEquals(0.5D, TEST.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.75D;
        Assertions.assertEquals(0.75D, TEST.next());
        Assertions.assertEquals(0.75D, TEST.next(0D, 1D));
        Assertions.assertEquals(0.6500000000000001D, TEST.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(MAXIMUM_SAFE_VALUE_LESS_THAN_ONE, TEST.next());
        Assertions.assertEquals(MAXIMUM_SAFE_VALUE_LESS_THAN_ONE, TEST.next(0D, 1D));
        Assertions.assertEquals(0.8D, TEST.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, TEST.next(0.5D, 0.5D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_number() {
        Assertions.assertEquals(0L, TEST.next(0L, 1L));
        Assertions.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.19D;
        Assertions.assertEquals(0L, TEST.next(0L, 1L));
        Assertions.assertEquals(0L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.2D;
        Assertions.assertEquals(1L, TEST.next(1L, 2L));
        Assertions.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.39D;
        Assertions.assertEquals(1L, TEST.next(1L, 2L));
        Assertions.assertEquals(1L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.4D;
        Assertions.assertEquals(2L, TEST.next(2L, 3L));
        Assertions.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.59D;
        Assertions.assertEquals(2L, TEST.next(2L, 3L));
        Assertions.assertEquals(2L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.6D;
        Assertions.assertEquals(3L, TEST.next(3L, 4L));
        Assertions.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.79D;
        Assertions.assertEquals(3L, TEST.next(3L, 4L));
        Assertions.assertEquals(3L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.8D;
        Assertions.assertEquals(4L, TEST.next(4L, 5L));
        Assertions.assertEquals(4L, TEST.next(0L, 5L));
        RANDOM_STATE.value = 0.99D;
        Assertions.assertEquals(4L, TEST.next(4L, 5L));
        Assertions.assertEquals(4L, TEST.next(0L, 5L));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(4L, TEST.next(4L, 5L));
        Assertions.assertEquals(4L, TEST.next(0L, 5L));
    }

    @Test
    public void GIVEN_a_bounded_random_number_generator_WHEN_generates_a_sometimes_with_an_additional_boundary_but_always_random_number_THEN_get_a_random_number() {
        RandomSupport test = TEST.bounded(0.2D, 0.8D);

        Assertions.assertEquals(0.2D, test.next());
        Assertions.assertEquals(0.2D, test.next(0D, 1D));
        Assertions.assertEquals(0.32000000000000006D, test.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, test.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.25D;
        Assertions.assertEquals(0.35000000000000003D, test.next());
        Assertions.assertEquals(0.35000000000000003D, test.next(0D, 1D));
        Assertions.assertEquals(0.41000000000000003D, test.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, test.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.5D;
        Assertions.assertEquals(0.5D, test.next());
        Assertions.assertEquals(0.5D, test.next(0D, 1D));
        Assertions.assertEquals(0.5D, test.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, test.next(0.5D, 0.5D));
        RANDOM_STATE.value = 0.75D;
        Assertions.assertEquals(0.6500000000000001D, test.next());
        Assertions.assertEquals(0.6500000000000001D, test.next(0D, 1D));
        Assertions.assertEquals(0.5900000000000001D, test.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, test.next(0.5D, 0.5D));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(0.8D, test.next());
        Assertions.assertEquals(0.8D, test.next(0D, 1D));
        Assertions.assertEquals(0.6800000000000002D, test.next(0.2D, 0.8D));
        Assertions.assertEquals(0.5D, test.next(0.5D, 0.5D));
    }

    @Test
    public void GIVEN_a_bounded_random_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_number() {
        RandomSupport test = TEST.bounded(0.2D, 0.8D);

        Assertions.assertEquals(1L, test.next(1L, 2L));
        Assertions.assertEquals(1L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.19D;
        Assertions.assertEquals(1L, test.next(1L, 2L));
        Assertions.assertEquals(1L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.2D;
        Assertions.assertEquals(1L, test.next(1L, 2L));
        Assertions.assertEquals(1L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.39D;
        Assertions.assertEquals(2L, test.next(2L, 3L));
        Assertions.assertEquals(2L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.4D;
        Assertions.assertEquals(2L, test.next(2L, 3L));
        Assertions.assertEquals(2L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.59D;
        Assertions.assertEquals(2L, test.next(2L, 3L));
        Assertions.assertEquals(2L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.6D;
        Assertions.assertEquals(2L, test.next(2L, 3L));
        Assertions.assertEquals(2L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.79D;
        Assertions.assertEquals(3L, test.next(3L, 4L));
        Assertions.assertEquals(3L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.8D;
        Assertions.assertEquals(3L, test.next(3L, 4L));
        Assertions.assertEquals(3L, test.next(0L, 5L));
        RANDOM_STATE.value = 0.99D;
        Assertions.assertEquals(3L, test.next(3L, 4L));
        Assertions.assertEquals(3L, test.next(0L, 5L));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertEquals(3L, test.next(3L, 4L));
        Assertions.assertEquals(4L, test.next(0L, 5L));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        RANDOM_STATE.value = 0.19D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        RANDOM_STATE.value = 0.39D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertTrue(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        RANDOM_STATE.value = 0.59D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertTrue(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertFalse(TEST.isBetween(0.75D, 1D));
        RANDOM_STATE.value = 0.79D;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertTrue(TEST.isBetween(0.75D, 1D));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isBetween(0D, Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0D, 0.25D));
        Assertions.assertFalse(TEST.isBetween(0.25D, 0.5D));
        Assertions.assertFalse(TEST.isBetween(0.5D, 0.75D));
        Assertions.assertTrue(TEST.isBetween(0.75D, 1D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        RANDOM_STATE.value = 0.19D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        RANDOM_STATE.value = 0.39D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertTrue(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        RANDOM_STATE.value = 0.59D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertTrue(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        RANDOM_STATE.value = 0.79D;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertFalse(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
        RANDOM_STATE.value = MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
        Assertions.assertFalse(TEST.isLessThan(Double.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25D));
        Assertions.assertFalse(TEST.isLessThan(0.5D));
        Assertions.assertFalse(TEST.isLessThan(0.75D));
        Assertions.assertTrue(TEST.isLessThan(1D));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_shuffling_items_in_place_THEN_shuffle_all_items() {
        DeterministicRandomSupport test = new DeterministicRandomSupport(2L);
        long size = 10L;

        List<Long> items = new ArrayList<>(LongStream.range(0, size)
                .mapToObj(index -> size - 1L - index)
                .toList());

        Assertions.assertEquals(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L, 0L), items);
        test.shuffle(items);
        Assertions.assertEquals(List.of(8L, 6L, 7L, 4L, 5L, 2L, 3L, 0L, 1L, 9L), items);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_creating_a_copy_of_the_shuffled_items_THEN_create_a_copy_of_the_shuffled_items() {
        DeterministicRandomSupport test = new DeterministicRandomSupport(2L);
        long size = 10L;

        List<Long> items = new ArrayList<>(LongStream.range(0, size)
                .mapToObj(index -> size - 1L - index)
                .toList());

        Assertions.assertEquals(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L, 0L), items);
        Assertions.assertEquals(List.of(8L, 6L, 7L, 4L, 5L, 2L, 3L, 0L, 1L, 9L), test.createShuffled(items, Long.class));
        Assertions.assertEquals(List.of(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L, 0L), items);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DoubleValue {
        private double value = 0D;
    }
}
