/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.data.structure.probabilistic.DefaultHashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.HashingFunctionAlgorithm;
import com.dipasquale.data.structure.probabilistic.HashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent.DefaultBloomFilterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class MultiPartBloomFilterTest {
    private static final int CONSISTENCY_CHECK_COUNT = 15;
    private static final HashingFunctionFactory HASHING_FUNCTION_FACTORY = new DefaultHashingFunctionFactory();
    private static final HashingFunction HASHING_FUNCTION = HASHING_FUNCTION_FACTORY.create(HashingFunctionAlgorithm.MD5, MultiPartBloomFilterTest.class.getSimpleName());
    private static final DefaultBloomFilterFactory BLOOM_FILTER_DEFAULT_FACTORY = new DefaultBloomFilterFactory(HASHING_FUNCTION);
    private static final MultiPartBloomFilterFactory BLOOM_FILTER_FACTORY = new MultiPartBloomFilterFactory(new LiteralBloomFilterPartitionFactory(BLOOM_FILTER_DEFAULT_FACTORY), 3);

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(3, 1, 0.5D);

        Assertions.assertFalse(test.mightContain("one"));
        Assertions.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK_COUNT; i++) {
            Assertions.assertTrue(test.mightContain("one"));
            Assertions.assertFalse(test.add("one"));
        }
    }

    @Test
    public void TEST_2() {
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
