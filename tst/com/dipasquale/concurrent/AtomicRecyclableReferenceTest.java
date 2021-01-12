package com.dipasquale.concurrent;

import com.dipasquale.common.ExpiryRecord;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.ObjectFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.SI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicRecyclableReferenceTest {
    private static final AtomicInteger REFERENCE_SEED = new AtomicInteger();
    private static final AtomicLong EXPIRY_SEED = new AtomicLong();
    private static final ExpirySupport EXPIRY_SUPPORT = () -> new ExpiryRecord(EXPIRY_SEED.get(), EXPIRY_SEED.get() + 1L, SI.MILLI(SI.SECOND));

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final RecyclableReference.Factory<String> objectFactory) {
        return new AtomicRecyclableReference<>(objectFactory, EXPIRY_SUPPORT);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final ObjectFactory<String> objectFactory) {
        return new AtomicRecyclableReference<>(objectFactory, EXPIRY_SUPPORT);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference(final String value) {
        return createAtomicRecyclableReference(() -> value);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference() {
        return createAtomicRecyclableReference(edt -> Integer.toString(REFERENCE_SEED.incrementAndGet()));
    }

    @Before
    public void before() {
        REFERENCE_SEED.set(0);
        EXPIRY_SEED.set(0L);
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_creating_an_atomic_reference_recyclable_THEN_avoid_invoking_the_factory_since_the_reference_is_not_needed_until_the_get_method_is_invoked() {
        createAtomicRecyclableReference();
        Assert.assertEquals(0, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_reference_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_as_long_as_it_is_not_expired() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals("1", test.reference());
        Assert.assertEquals("1", test.reference());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_reference_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_multiple_times_once_every_time_the_reference_expires() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals("1", test.reference());
        EXPIRY_SEED.incrementAndGet();
        Assert.assertEquals("2", test.reference());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_hash_code_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_to_base_it_off_the_reference() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("1");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(expected.hashCode(), test.hashCode());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_comparing_2_atomic_reference_recyclables_THEN_rely_on_invoking_the_factory_once_per_atomic_reference_recyclable_to_compare_them() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("1");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(expected, test);
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_comparing_the_same_atomic_reference_recyclable_twice_THEN_rely_on_invoking_the_factory_once_per_atomic_reference_recyclable_to_compare_them() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(test, test);
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_comparing_2_atomic_reference_recyclables_that_are_not_the_same_THEN_rely_on_invoking_the_factory_once_per_atomic_reference_recyclable_to_compare_them() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("2");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertNotEquals(expected, test);
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_getting_the_string_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_to_base_it_off_the_reference() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals("1", test.toString());
    }
}
