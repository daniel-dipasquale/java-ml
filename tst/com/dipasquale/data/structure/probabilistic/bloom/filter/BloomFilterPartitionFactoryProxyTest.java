package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class BloomFilterPartitionFactoryProxyTest {
    private static final AtomicInteger MAXIMUM_HASH_FUNCTIONS = new AtomicInteger();
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterPartitionFactory BLOOM_FILTER_PARTITION_FACTORY = new BloomFilterPartitionFactoryProxyMock(MAXIMUM_HASH_FUNCTIONS, ITEMS);

    @BeforeEach
    public void beforeEach() {
        MAXIMUM_HASH_FUNCTIONS.set(21);
        ITEMS.clear();
    }

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertTrue(test.mightContain("index"));
        Assertions.assertTrue(test.mightContain("estimatedSize"));
        Assertions.assertTrue(test.mightContain("hashingFunctionCount"));
        Assertions.assertTrue(test.mightContain("falsePositiveRatio"));
        Assertions.assertTrue(test.mightContain("size"));
        Assertions.assertFalse(test.add("size"));
        Assertions.assertTrue(test.add("does-not-exist"));
    }

    @Test
    public void TEST_2() {
        BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertEquals(ImmutableMap.<String, String>builder()
                .put("index", "5")
                .put("estimatedSize", "2000000")
                .put("hashingFunctionCount", "21")
                .put("falsePositiveRatio", "0.5")
                .put("size", "1000000")
                .build(), ITEMS);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BloomFilterPartitionFactoryProxyMock implements BloomFilterPartitionFactory {
        private final AtomicInteger hashingFunctionCount;
        private final Map<String, String> items;

        @Override
        public int getHashingFunctionCount() {
            return hashingFunctionCount.get();
        }

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
            items.put("index", Integer.toString(index));
            items.put("estimatedSize", Integer.toString(estimatedSize));
            items.put("hashingFunctionCount", Integer.toString(hashingFunctionCount));
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
