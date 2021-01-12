package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class BloomFilterPartitionFactoryTest {
    private static final AtomicInteger MAXIMUM_HASH_FUNCTIONS = new AtomicInteger();
    private static final Map<String, String> ITEMS = new HashMap<>();

    private static final BloomFilterFactory BLOOM_FILTER_FACTORY = new BloomFilterFactory() {
        @Override
        public int getMaximumHashFunctions() {
            return MAXIMUM_HASH_FUNCTIONS.get();
        }

        private <T> T ensureType(final Object object) {
            return (T) object;
        }

        @Override
        public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
            ITEMS.put("estimatedSize", Integer.toString(estimatedSize));
            ITEMS.put("hashFunctions", Integer.toString(hashFunctions));
            ITEMS.put("falsePositiveRatio", Double.toString(falsePositiveRatio));
            ITEMS.put("size", Long.toString(size));

            return ensureType(new BloomFilter<String>() {
                @Override
                public boolean mightContain(final String item) {
                    return ITEMS.containsKey(item);
                }

                @Override
                public boolean add(final String item) {
                    return ITEMS.put(item, item) == null;
                }
            });
        }
    };

    private static final BloomFilterPartitionFactory TEST = BloomFilterPartitionFactory.create(BLOOM_FILTER_FACTORY);

    @Before
    public void before() {
        MAXIMUM_HASH_FUNCTIONS.set(21);
        ITEMS.clear();
    }

    @Test
    public void TEST_1() {
        Assert.assertEquals(21, TEST.getMaximumHashFunctions());
        MAXIMUM_HASH_FUNCTIONS.set(7);
        Assert.assertEquals(7, TEST.getMaximumHashFunctions());
    }

    @Test
    public void TEST_2() {
        BloomFilter<String> result = TEST.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assert.assertTrue(result.mightContain("estimatedSize"));
        Assert.assertTrue(result.mightContain("hashFunctions"));
        Assert.assertTrue(result.mightContain("falsePositiveRatio"));
        Assert.assertTrue(result.mightContain("size"));
        Assert.assertFalse(result.add("size"));
        Assert.assertTrue(result.add("does-not-exist"));
    }

    @Test
    public void TEST_3() {
        TEST.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assert.assertEquals(ImmutableMap.<String, String>builder()
                .put("estimatedSize", "2000000")
                .put("hashFunctions", "21")
                .put("falsePositiveRatio", "0.5")
                .put("size", "1000000")
                .build(), ITEMS);
    }
}
