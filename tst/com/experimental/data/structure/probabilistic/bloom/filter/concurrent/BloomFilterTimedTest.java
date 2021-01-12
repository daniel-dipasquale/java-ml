//package com.pasqud.data.structure.probabilistic.bloom.filter.concurrent;
//
//import com.experimental.data.structure.probabilistic.bloom.filter.concurrent.BloomFilterFactoryTimed;
//import com.pasqud.common.ExpiryRecord;
//import com.pasqud.common.ExpirySupport;
//import com.pasqud.data.structure.probabilistic.bloom.filter.BloomFilter;
//import com.pasqud.data.structure.probabilistic.MultiFunctionHashing;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.measure.unit.SI;
//import java.util.concurrent.atomic.AtomicLong;
//
//public final class BloomFilterTimedTest {
//    private static final MultiFunctionHashing MULTI_FUNCTION_HASHING = MultiFunctionHashing.createSha512(25, BloomFilterTimedRecyclableTest.class.getSimpleName());
//    private static final AtomicLong EXPIRY_SEED = new AtomicLong();
//    private static final ExpirySupport EXPIRY_SUPPORT = () -> new ExpiryRecord(EXPIRY_SEED.get(), EXPIRY_SEED.get() + 1L, SI.MILLI(SI.SECOND));
//    private static final BloomFilterFactoryTimed BLOOM_FILTER_FACTORY = new BloomFilterFactoryTimed(MULTI_FUNCTION_HASHING, EXPIRY_SUPPORT);
//
//    @Before
//    public void before() {
//        EXPIRY_SEED.set(1L);
//    }
//
//    @Test
//    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_before_and_after_it_is_added_THEN_determine_if_it_might_exist_before_and_after_it_is_added() {
//        BloomFilter<String> test = BLOOM_FILTER_FACTORY.create(1, 1, 0.5D);
//
//        Assert.assertFalse(test.mightContain("one"));
//        Assert.assertTrue(test.add("one"));
//        Assert.assertTrue(test.mightContain("one"));
//        Assert.assertFalse(test.add("one"));
//    }
//
//    @Test
//    public void GIVEN_an_empty_bloom_filter_WHEN_determining_if_an_item_might_exist_after_it_is_added_THEN_determine_it_might_not_contain_it_due_to_expiration() {
//        BloomFilter<String> test = BLOOM_FILTER_FACTORY.create(1, 1, 0.5D);
//
//        Assert.assertFalse(test.mightContain("one"));
//        Assert.assertTrue(test.add("one"));
//        EXPIRY_SEED.incrementAndGet();
//        Assert.assertFalse(test.mightContain("one"));
//        Assert.assertTrue(test.add("one"));
//    }
//
//    @Test
//    public void GIVEN_an_empty_bloom_filter_WHEN_adding_different_items_THEN_ensure_the_items_do_not_overlap() {
//        BloomFilter<String> test = BLOOM_FILTER_FACTORY.create(4);
//
//        Assert.assertFalse(test.mightContain("one"));
//        Assert.assertTrue(test.add("one"));
//        Assert.assertFalse(test.mightContain("two"));
//        Assert.assertTrue(test.add("two"));
//        Assert.assertFalse(test.mightContain("three"));
//        Assert.assertTrue(test.add("three"));
//    }
//}
