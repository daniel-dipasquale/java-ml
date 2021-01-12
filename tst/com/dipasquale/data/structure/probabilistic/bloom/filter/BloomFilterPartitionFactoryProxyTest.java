package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class BloomFilterPartitionFactoryProxyTest {
    private static final AtomicInteger MAXIMUM_HASH_FUNCTIONS = new AtomicInteger();
    private static final Map<String, String> ITEMS = new HashMap<>();

    private static final BloomFilterPartitionFactory BLOOM_FILTER_PARTITION_FACTORY = new BloomFilterPartitionFactory() {
        @Override
        public int getMaximumHashFunctions() {
            return MAXIMUM_HASH_FUNCTIONS.get();
        }

        private <T> T ensureType(final Object object) {
            return (T) object;
        }

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
            ITEMS.put("index", Integer.toString(index));
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

    @Before
    public void before() {
        MAXIMUM_HASH_FUNCTIONS.set(21);
        ITEMS.clear();
    }

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assert.assertTrue(test.mightContain("index"));
        Assert.assertTrue(test.mightContain("estimatedSize"));
        Assert.assertTrue(test.mightContain("hashFunctions"));
        Assert.assertTrue(test.mightContain("falsePositiveRatio"));
        Assert.assertTrue(test.mightContain("size"));
        Assert.assertFalse(test.add("size"));
        Assert.assertTrue(test.add("does-not-exist"));
    }

    @Test
    public void TEST_2() {
        BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assert.assertEquals(ImmutableMap.<String, String>builder()
                .put("index", "5")
                .put("estimatedSize", "2000000")
                .put("hashFunctions", "21")
                .put("falsePositiveRatio", "0.5")
                .put("size", "1000000")
                .build(), ITEMS);
    }
}
