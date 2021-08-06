package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class BloomFilterPartitionFactoryTest {
    private static final AtomicInteger MAXIMUM_HASH_FUNCTIONS = new AtomicInteger();
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterFactory BLOOM_FILTER_FACTORY = new BloomFilterFactoryMock(MAXIMUM_HASH_FUNCTIONS, ITEMS);
    private static final BloomFilterPartitionFactory TEST = new DefaultBloomFilterPartitionFactory(BLOOM_FILTER_FACTORY);

    @BeforeEach
    public void beforeEach() {
        MAXIMUM_HASH_FUNCTIONS.set(21);
        ITEMS.clear();
    }

    @Test
    public void TEST_1() {
        Assertions.assertEquals(21, TEST.getHashingFunctionCount());
        MAXIMUM_HASH_FUNCTIONS.set(7);
        Assertions.assertEquals(7, TEST.getHashingFunctionCount());
    }

    @Test
    public void TEST_2() {
        BloomFilter<String> result = TEST.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertTrue(result.mightContain("estimatedSize"));
        Assertions.assertTrue(result.mightContain("hashingFunctionCount"));
        Assertions.assertTrue(result.mightContain("falsePositiveRatio"));
        Assertions.assertTrue(result.mightContain("size"));
        Assertions.assertFalse(result.add("size"));
        Assertions.assertTrue(result.add("does-not-exist"));
    }

    @Test
    public void TEST_3() {
        TEST.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertEquals(ImmutableMap.<String, String>builder()
                .put("estimatedSize", "2000000")
                .put("hashingFunctionCount", "21")
                .put("falsePositiveRatio", "0.5")
                .put("size", "1000000")
                .build(), ITEMS);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor
    private static final class BloomFilterFactoryMock implements BloomFilterFactory {
        private final AtomicInteger hashingFunctionCount;
        private final Map<String, String> items;

        @Override
        public int getHashingFunctionCount() {
            return hashingFunctionCount.get();
        }

        @Override
        public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
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
