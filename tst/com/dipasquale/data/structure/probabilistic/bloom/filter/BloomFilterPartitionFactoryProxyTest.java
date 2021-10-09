package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public final class BloomFilterPartitionFactoryProxyTest {
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterPartitionFactory BLOOM_FILTER_PARTITION_FACTORY = new BloomFilterPartitionFactoryProxyMock(ITEMS);

    @BeforeEach
    public void beforeEach() {
        ITEMS.clear();
    }

    @Test
    public void TEST_1() {
        BloomFilter<String> test = BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertTrue(test.mightContain("index"));
        Assertions.assertTrue(test.mightContain("estimatedSize"));
        Assertions.assertTrue(test.mightContain("hashingFunctions"));
        Assertions.assertTrue(test.mightContain("falsePositiveRatio"));
        Assertions.assertTrue(test.mightContain("size"));
        Assertions.assertFalse(test.add("size"));
        Assertions.assertTrue(test.add("does-not-exist"));
    }

    @Test
    public void TEST_2() {
        BLOOM_FILTER_PARTITION_FACTORY.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertEquals(Map.ofEntries(
                Map.entry("index", "5"),
                Map.entry("estimatedSize", "2000000"),
                Map.entry("hashingFunctions", "21"),
                Map.entry("falsePositiveRatio", "0.5"),
                Map.entry("size", "1000000")
        ), ITEMS);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BloomFilterPartitionFactoryProxyMock implements BloomFilterPartitionFactory {
        private final Map<String, String> items;

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
            items.put("index", Integer.toString(index));
            items.put("estimatedSize", Integer.toString(estimatedSize));
            items.put("hashingFunctions", Integer.toString(hashingFunctions));
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
