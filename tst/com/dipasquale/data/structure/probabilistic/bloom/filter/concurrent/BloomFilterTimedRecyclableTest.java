package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.SI;
import java.util.concurrent.atomic.AtomicLong;

public final class BloomFilterTimedRecyclableTest {
    private static final int CONSISTENCY_CHECK = 15;
    private static final MultiFunctionHashing MULTI_FUNCTION_HASHING = MultiFunctionHashing.createMd5(25, BloomFilterTimedRecyclableTest.class.getSimpleName());
    private static final BloomFilterDefaultFactory BLOOM_FILTER_DEFAULT_FACTORY = new BloomFilterDefaultFactory(MULTI_FUNCTION_HASHING);
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.create(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));
    private static final ExpirySupport EXPIRY_SUPPORT = ExpirySupport.create(DATE_TIME_SUPPORT, 1L);
    private static final BloomFilterTimedRecyclableFactory BLOOM_FILTER_FACTORY = new BloomFilterTimedRecyclableFactory(BLOOM_FILTER_DEFAULT_FACTORY, EXPIRY_SUPPORT);

    @Before
    public void before() {
        CURRENT_DATE_TIME.set(1L);
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_before_and_after_it_is_added_THEN_determine_if_it_might_exist_before_and_after_it_is_added() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(1, 1, 0.5D);

        Assert.assertFalse(test.mightContain("one"));
        Assert.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
        }
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_after_it_is_added_THEN_determine_it_might_not_contain_it_due_to_expiration() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(1, 1, 0.5D);


        Assert.assertFalse(test.mightContain("one"));
        Assert.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
        }

        CURRENT_DATE_TIME.incrementAndGet();
        Assert.assertFalse(test.mightContain("one"));
        Assert.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
        }
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_adding_different_items_THEN_ensure_the_items_do_not_overlap() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(3);

        Assert.assertFalse(test.mightContain("one"));
        Assert.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
        }

        Assert.assertFalse(test.mightContain("two"));
        Assert.assertTrue(test.add("two"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("two"));
            Assert.assertFalse(test.add("two"));
        }

        Assert.assertFalse(test.mightContain("three"));
        Assert.assertTrue(test.add("three"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("three"));
            Assert.assertFalse(test.add("three"));
        }

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
            Assert.assertTrue(test.mightContain("two"));
            Assert.assertFalse(test.add("two"));
            Assert.assertTrue(test.mightContain("three"));
            Assert.assertFalse(test.add("three"));
        }
    }
}
