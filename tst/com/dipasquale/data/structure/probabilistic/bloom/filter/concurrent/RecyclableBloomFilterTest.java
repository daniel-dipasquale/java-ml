package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.HashingFunctionAlgorithm;
import com.dipasquale.data.structure.probabilistic.HashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.JdkHashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class RecyclableBloomFilterTest {
    private static final int CONSISTENCY_CHECK_COUNT = 15;
    private static final HashingFunctionFactory HASHING_FUNCTION_FACTORY = new JdkHashingFunctionFactory();
    private static final HashingFunction HASHING_FUNCTION = HASHING_FUNCTION_FACTORY.create(HashingFunctionAlgorithm.MD5, RecyclableBloomFilterTest.class.getSimpleName());
    private static final AtomicLongArrayBloomFilterFactory BLOOM_FILTER_DEFAULT_FACTORY = new AtomicLongArrayBloomFilterFactory(HASHING_FUNCTION);
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::get, TimeUnit.MILLISECONDS);
    private static final ExpirationFactory EXPIRY_SUPPORT = DATE_TIME_SUPPORT.createBucketExpirationFactory(1L);
    private static final RecyclableBloomFilterFactory BLOOM_FILTER_FACTORY = new RecyclableBloomFilterFactory(BLOOM_FILTER_DEFAULT_FACTORY, EXPIRY_SUPPORT);

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(1L);
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_before_and_after_it_is_added_THEN_determine_if_it_might_exist_before_and_after_it_is_added() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(1, 1, 0.5D);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_after_it_is_added_THEN_determine_it_might_not_contain_it_due_to_expiration() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(1, 1, 0.5D);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }

        CURRENT_DATE_TIME.incrementAndGet();
        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }
    }

    @Test
    public void GIVEN_an_empty_bloom_filter_WHEN_adding_different_items_THEN_ensure_the_items_do_not_overlap() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(3);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }

        Assertions.assertFalse(test.mightContain("two"));
        Assertions.assertTrue(test.add("two"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("two"));
            Assertions.assertFalse(test.add("two"));
        }

        Assertions.assertFalse(test.mightContain("three"));
        Assertions.assertTrue(test.add("three"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("three"));
            Assertions.assertFalse(test.add("three"));
        }

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
            Assertions.assertTrue(test.mightContain("two"));
            Assertions.assertFalse(test.add("two"));
            Assertions.assertTrue(test.mightContain("three"));
            Assertions.assertFalse(test.add("three"));
        }
    }
}
