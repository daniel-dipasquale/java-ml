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
        return createAtomicRecyclableReference(edt -> Integer.toString(REFERENCE_SEED.incrementAndGet()), collectRecycledReferences);
    }

    private static AtomicRecyclableReference<String> createAtomicRecyclableReference() {
        return createAtomicRecyclableReference(false);
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

        Assert.assertEquals(0, REFERENCE_SEED.get());
        Assert.assertEquals("1", test.reference());
        Assert.assertEquals(1, REFERENCE_SEED.get());
        Assert.assertEquals("1", test.reference());
        Assert.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_reference_is_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_multiple_times_once_every_time_the_reference_expires() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(0, REFERENCE_SEED.get());
        Assert.assertEquals("1", test.reference());
        Assert.assertEquals(1, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assert.assertEquals("2", test.reference());
        Assert.assertEquals(2, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_the_hash_code_and_equality_and_string_representation_are_needed_from_the_atomic_reference_recyclable_THEN_rely_on_invoking_the_factory_once_to_base_it_off_the_reference() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("1");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(0, REFERENCE_SEED.get());
        Assert.assertEquals(expected.hashCode(), test.hashCode());
        Assert.assertEquals(1, REFERENCE_SEED.get());
        Assert.assertEquals(expected, test);
        Assert.assertEquals(1, REFERENCE_SEED.get());
        Assert.assertEquals(test, test);
        Assert.assertEquals(1, REFERENCE_SEED.get());
        Assert.assertEquals("1", test.toString());
        Assert.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void GIVEN_a_factory_that_keeps_track_of_the_times_it_is_called_WHEN_comparing_2_atomic_reference_recyclables_that_are_not_the_same_THEN_rely_on_invoking_the_factory_once_per_atomic_reference_recyclable_to_compare_them() {
        AtomicRecyclableReference<String> expected = createAtomicRecyclableReference("2");
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference();

        Assert.assertEquals(0, REFERENCE_SEED.get());
        Assert.assertNotEquals(expected, test);
        Assert.assertEquals(1, REFERENCE_SEED.get());
        Assert.assertNotEquals(expected, new Object());
        Assert.assertEquals(1, REFERENCE_SEED.get());
    }

    @Test
    public void TEST_1() {
        AtomicRecyclableReference<String> test = createAtomicRecyclableReference(true);

        Assert.assertNull(test.pollRecycled());
        Assert.assertEquals(0, REFERENCE_SEED.get());
        Assert.assertEquals("1", test.reference());
        Assert.assertNull(test.pollRecycled());
        Assert.assertEquals(1, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assert.assertEquals("2", test.reference());
        Assert.assertEquals(new RecyclableReference<>("1", 1), test.pollRecycled());
        Assert.assertNull(test.pollRecycled());
        Assert.assertEquals(2, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assert.assertEquals("3", test.reference());
        Assert.assertEquals(3, REFERENCE_SEED.get());
        EXPIRY_SEED.incrementAndGet();
        Assert.assertEquals("4", test.reference());
        Assert.assertEquals(new RecyclableReference<>("2", 2), test.pollRecycled());
        Assert.assertEquals(new RecyclableReference<>("3", 3), test.pollRecycled());
        Assert.assertNull(test.pollRecycled());
        Assert.assertEquals(4, REFERENCE_SEED.get());
    }
}
