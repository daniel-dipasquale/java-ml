package com.dipasquale.data.structure.probabilistic.bloom.filter;

import org.junit.Assert;
import org.junit.Test;

public class BloomFilterGuavaTest {
    private static final int CONSISTENCY_CHECK = 15;
    private static final BloomFilterDefaultFactory BLOOM_FILTER_FACTORY = new BloomFilterDefaultFactory();

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_FACTORY.createEstimated(1, 1, 0.5D);

        Assert.assertFalse(test.mightContain("one"));
        Assert.assertTrue(test.add("one"));

        for (int i = 0; i < CONSISTENCY_CHECK; i++) {
            Assert.assertTrue(test.mightContain("one"));
            Assert.assertFalse(test.add("one"));
        }
    }

    @Test
    public void TEST_2() {
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
