package com.dipasquale.common.random.float1;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class RandomSupportTest {
    private static final float MAXIMUM_VALUE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final FloatValue RANDOM_STATE = new FloatValue();
    private static final RandomSupport TEST = () -> RANDOM_STATE.value;

    @BeforeEach
    public void beforeEach() {
        RANDOM_STATE.value = 0f;
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generates_a_sometimes_bounded_but_always_random_number_THEN_get_a_random_number() {
        Assertions.assertEquals(0f, TEST.next(), 0f);
        Assertions.assertEquals(0f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.2f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.25f;
        Assertions.assertEquals(0.25f, TEST.next(), 0f);
        Assertions.assertEquals(0.25f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.35000002f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.5f;
        Assertions.assertEquals(0.5f, TEST.next(), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.75f;
        Assertions.assertEquals(0.75f, TEST.next(), 0f);
        Assertions.assertEquals(0.75f, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.65000004f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertEquals(MAXIMUM_VALUE, TEST.next(), 0f);
        Assertions.assertEquals(MAXIMUM_VALUE, TEST.next(0f, 1f), 0f);
        Assertions.assertEquals(0.79999995f, TEST.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, TEST.next(0.5f, 0.5f), 0f);
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_number() {
        Assertions.assertEquals(0, TEST.next(0, 1));
        Assertions.assertEquals(0, TEST.next(0, 5));
        RANDOM_STATE.value = 0.19f;
        Assertions.assertEquals(0, TEST.next(0, 1));
        Assertions.assertEquals(0, TEST.next(0, 5));
        RANDOM_STATE.value = 0.2f;
        Assertions.assertEquals(1, TEST.next(1, 2));
        Assertions.assertEquals(1, TEST.next(0, 5));
        RANDOM_STATE.value = 0.39f;
        Assertions.assertEquals(1, TEST.next(1, 2));
        Assertions.assertEquals(1, TEST.next(0, 5));
        RANDOM_STATE.value = 0.4f;
        Assertions.assertEquals(2, TEST.next(2, 3));
        Assertions.assertEquals(2, TEST.next(0, 5));
        RANDOM_STATE.value = 0.59f;
        Assertions.assertEquals(2, TEST.next(2, 3));
        Assertions.assertEquals(2, TEST.next(0, 5));
        RANDOM_STATE.value = 0.6f;
        Assertions.assertEquals(3, TEST.next(3, 4));
        Assertions.assertEquals(3, TEST.next(0, 5));
        RANDOM_STATE.value = 0.79f;
        Assertions.assertEquals(3, TEST.next(3, 4));
        Assertions.assertEquals(3, TEST.next(0, 5));
        RANDOM_STATE.value = 0.8f;
        Assertions.assertEquals(4, TEST.next(4, 5));
        Assertions.assertEquals(4, TEST.next(0, 5));
        RANDOM_STATE.value = 0.99f;
        Assertions.assertEquals(4, TEST.next(4, 5));
        Assertions.assertEquals(4, TEST.next(0, 5));
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertEquals(4, TEST.next(4, 5));
        Assertions.assertEquals(4, TEST.next(0, 5));
    }

    @Test
    public void GIVEN_a_bounded_random_number_generator_WHEN_generates_a_sometimes_with_an_additional_boundary_but_always_random_number_THEN_get_a_random_number() {
        RandomSupport test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertEquals(0.2f, test.next(), 0f);
        Assertions.assertEquals(0.2f, test.next(0f, 1f), 0f);
        Assertions.assertEquals(0.32f, test.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.25f;
        Assertions.assertEquals(0.35000002f, test.next(), 0f);
        Assertions.assertEquals(0.35000002f, test.next(0f, 1f), 0f);
        Assertions.assertEquals(0.41000003f, test.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.5f;
        Assertions.assertEquals(0.5f, test.next(), 0f);
        Assertions.assertEquals(0.5f, test.next(0f, 1f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = 0.75f;
        Assertions.assertEquals(0.65000004f, test.next(), 0f);
        Assertions.assertEquals(0.65000004f, test.next(0f, 1f), 0f);
        Assertions.assertEquals(0.59000003f, test.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.5f, 0.5f), 0f);
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertEquals(0.7999999523162842f, test.next(), 0f);
        Assertions.assertEquals(0.7999999523162842f, test.next(0f, 1f), 0f);
        Assertions.assertEquals(0.68f, test.next(0.2f, 0.8f), 0f);
        Assertions.assertEquals(0.5f, test.next(0.5f, 0.5f), 0f);
    }

    @Test
    public void GIVEN_a_bounded_random_number_generator_WHEN_generates_a_bounded_random_but_whole_number_THEN_get_a_random_number() {
        RandomSupport test = TEST.bounded(0.2f, 0.8f);

        Assertions.assertEquals(1, test.next(1, 2));
        Assertions.assertEquals(1, test.next(0, 5));
        RANDOM_STATE.value = 0.19f;
        Assertions.assertEquals(1, test.next(1, 2));
        Assertions.assertEquals(1, test.next(0, 5));
        RANDOM_STATE.value = 0.2f;
        Assertions.assertEquals(1, test.next(1, 2));
        Assertions.assertEquals(1, test.next(0, 5));
        RANDOM_STATE.value = 0.39f;
        Assertions.assertEquals(2, test.next(2, 3));
        Assertions.assertEquals(2, test.next(0, 5));
        RANDOM_STATE.value = 0.4f;
        Assertions.assertEquals(2, test.next(2, 3));
        Assertions.assertEquals(2, test.next(0, 5));
        RANDOM_STATE.value = 0.59f;
        Assertions.assertEquals(2, test.next(2, 3));
        Assertions.assertEquals(2, test.next(0, 5));
        RANDOM_STATE.value = 0.6f;
        Assertions.assertEquals(2, test.next(2, 3));
        Assertions.assertEquals(2, test.next(0, 5));
        RANDOM_STATE.value = 0.79f;
        Assertions.assertEquals(3, test.next(3, 4));
        Assertions.assertEquals(3, test.next(0, 5));
        RANDOM_STATE.value = 0.8f;
        Assertions.assertEquals(3, test.next(3, 4));
        Assertions.assertEquals(3, test.next(0, 5));
        RANDOM_STATE.value = 0.99f;
        Assertions.assertEquals(3, test.next(3, 4));
        Assertions.assertEquals(3, test.next(0, 5));
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertEquals(3, test.next(3, 4));
        Assertions.assertEquals(3, test.next(0, 5));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_an_expected_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        RANDOM_STATE.value = 0.19f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        RANDOM_STATE.value = 0.39f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertTrue(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        RANDOM_STATE.value = 0.59f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertTrue(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertFalse(TEST.isBetween(0.75f, 1f));
        RANDOM_STATE.value = 0.79f;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertTrue(TEST.isBetween(0.75f, 1f));
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertFalse(TEST.isBetween(0f, Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isBetween(0f, 0.25f));
        Assertions.assertFalse(TEST.isBetween(0.25f, 0.5f));
        Assertions.assertFalse(TEST.isBetween(0.5f, 0.75f));
        Assertions.assertTrue(TEST.isBetween(0.75f, 1f));
    }

    @Test
    public void GIVEN_a_random_number_generator_WHEN_determining_if_the_next_random_number_is_within_0_and_some_maximum_boundary_THEN_indicate_true_if_within_and_false_otherwise() {
        Assertions.assertTrue(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        RANDOM_STATE.value = 0.19f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertTrue(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        RANDOM_STATE.value = 0.39f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertTrue(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        RANDOM_STATE.value = 0.59f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertTrue(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        RANDOM_STATE.value = 0.79f;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertFalse(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
        RANDOM_STATE.value = MAXIMUM_VALUE;
        Assertions.assertFalse(TEST.isLessThan(Float.MIN_VALUE));
        Assertions.assertFalse(TEST.isLessThan(0.25f));
        Assertions.assertFalse(TEST.isLessThan(0.5f));
        Assertions.assertFalse(TEST.isLessThan(0.75f));
        Assertions.assertTrue(TEST.isLessThan(1f));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class FloatValue {
        private float value;
    }
}
