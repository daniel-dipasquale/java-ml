package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicRecyclableReferenceTest {
    private static final AtomicInteger REFERENCE_SEED = new AtomicInteger();
    private static final AtomicLong EXPIRY_SEED = new AtomicLong();
    private static final ExpirationFactory EXPIRY_SUPPORT = () -> new ExpirationRecord(EXPIRY_SEED.get(), EXPIRY_SEED.get() + 1L, TimeUnit.MILLISECONDS);

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final RecyclableReference.Factory<String> objectFactory, final boolean collectRecycledReferences) {
        return new AtomicRecyclableReference<>(objectFactory, EXPIRY_SUPPORT, collectRecycledReferences);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final RecyclableReference.Factory<String> objectFactory) {
        return createAtomicRecyclableReference(objectFactory, false);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final ObjectFactory<String> objectFactory) {
        return new AtomicRecyclableReference<>(objectFactory, EXPIRY_SUPPORT);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final String value) {
        return createAtomicRecyclableReference(() -> value);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final boolean collectRecycledReferences) {
        return createAtomicRecyclableReference(__ -> Integer.toString(REFERENCE_SEED.incrementAndGet()), collectRecycledReferences);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference() {
        return createAtomicRecyclableReference(false);
    }

    @BeforeEach
    public void beforeEach() {
        REFERENCE_SEED.set(0);
        EXPIRY_SEED.set(0L);
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_creating_an_atomic_reference_recyclable_THEN_avoid_invoking_the_factory_since_the_reference_is_not_needed_until_the_get_method_is_invoked() {
        createAtomicRecyclableReference();
        Assertions.assertEquals(0, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_reference_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_as_long_as_it_is_not_expired() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assertions.assertEquals(0, REFERENCE_SEED.get());
        Assertions.assertEquals("1", test.reference());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        Assertions.assertEquals("1", test.reference());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_reference_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_multiple_times_once_every_time_the_reference_expires() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assertions.assertEquals(0, REFERENCE_SEED.get());
        Assertions.assertEquals("1", test.reference());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assertions.assertEquals("2", test.reference());
        Assertions.assertEquals(2, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_hash_code_and_equality_and_string_representation_are_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_to_base_it_off_the_reference() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("1");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assertions.assertEquals(0, REFERENCE_SEED.get());
        Assertions.assertEquals(expected.hashCode(), test.hashCode());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        Assertions.assertEquals(expected, test);
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        Assertions.assertEquals(test, test);
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        Assertions.assertEquals("1", test.toString());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_comparing_2_atomic_reference_recyclables_that_are_not_the_same_THEN_rely_on_invoking_the_factory_once_per_atomic_reference_recyclable_to_compare_them() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("2");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assertions.assertEquals(0, REFERENCE_SEED.get());
        Assertions.assertNotEquals(expected, test);
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        Assertions.assertNotEquals(expected, new Object());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void TEST_1() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference(true);

        Assertions.assertNull(test.pollRecycled());
        Assertions.assertEquals(0, REFERENCE_SEED.get());
        Assertions.assertEquals("1", test.reference());
        Assertions.assertNull(test.pollRecycled());
        Assertions.assertEquals(1, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assertions.assertEquals("2", test.reference());
        Assertions.assertEquals(new RecyclableReference<>("1", 1), test.pollRecycled());
        Assertions.assertNull(test.pollRecycled());
        Assertions.assertEquals(2, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assertions.assertEquals("3", test.reference());
        Assertions.assertEquals(3, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assertions.assertEquals("4", test.reference());
        Assertions.assertEquals(new RecyclableReference<>("2", 2), test.pollRecycled());
        Assertions.assertEquals(new RecyclableReference<>("3", 3), test.pollRecycled());
        Assertions.assertNull(test.pollRecycled());
        Assertions.assertEquals(4, REFERENCE_SEED.get());
    }
}
