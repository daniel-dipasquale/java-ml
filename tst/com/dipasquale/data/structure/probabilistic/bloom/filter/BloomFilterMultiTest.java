package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BloomFilterMultiTest {
    private static final int CONSISTENCY_CHECK = 15;
    private static final MultiFunctionHashing MULTI_FUNCTION_HASHING = MultiFunctionHashing.createMd5(25, BloomFilterTimedRecyclableTest.class.getSimpleName());
    private static final BloomFilterDefaultFactory BLOOM_FILTER_DEFAULT_FACTORY = new BloomFilterDefaultFactory(MULTI_FUNCTION_HASHING);
    private static final BloomFilterMultiFactory BLOOM_FILTER_FACTORY = new BloomFilterMultiFactory(BloomFilterPartitionFactory.create(BLOOM_FILTER_DEFAULT_FACTORY), 3);

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(3, 1, 0.5D);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }
    }

    @Test
    public void TEST_2() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(3);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }

        Assertions.assertFalse(test.mightContain("two"));
        Assertions.assertTrue(test.add("two"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assertions.assertTrue(test.mightContain("two"));
            Assertions.assertFalse(test.add("two"));
        }

        Assertions.assertFalse(test.mightContain("three"));
        Assertions.assertTrue(test.add("three"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assertions.assertTrue(test.mightContain("three"));
            Assertions.assertFalse(test.add("three"));
        }

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
            Assertions.assertTrue(test.mightContain("two"));
            Assertions.assertFalse(test.add("two"));
            Assertions.assertTrue(test.mightContain("three"));
            Assertions.assertFalse(test.add("three"));
        }
    }
}
