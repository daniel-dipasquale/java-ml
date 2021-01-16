package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class BloomFilterPartitionFactoryProxyTest {
    private static final AtomicInteger MAXIMUM_HASH_FUNCTIONS = new AtomicInteger();
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterPartitionFactory BLOOM_FILTER_PARTITION_FACTORY = new BloomFilterPartitionFactoryProxyMock(MAXIMUM_HASH_FUNCTIONS, ITEMS);

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

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor
    private static final class BloomFilterPartitionFactoryProxyMock implements BloomFilterPartitionFactory {
        private final AtomicInteger maximumHashFunctions;
        private final Map<String, String> items;

        @Override
        public int getMaximumHashFunctions() {
            return maximumHashFunctions.get();
        }

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
            items.put("index", Integer.toString(index));
            items.put("estimatedSize", Integer.toString(estimatedSize));
            items.put("hashFunctions", Integer.toString(hashFunctions));
            items.put("falsePositiveRatio", Double.toString(falsePositiveRatio));
            items.put("size", Long.toString(size));

            return ensureType(new BloomFilter<String>() {
                @Override
                public boolean mightContain(final String item) {
                    return items.containsKey(item);
                }

                @Override
                public boolean add(final String item) {
                    return items.put(item, item) == null;
                }
            });
        }
    }
}
