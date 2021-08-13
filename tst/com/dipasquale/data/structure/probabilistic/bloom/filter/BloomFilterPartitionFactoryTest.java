/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public final class BloomFilterPartitionFactoryTest {
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterFactory BLOOM_FILTER_FACTORY = new BloomFilterFactoryMock(ITEMS);
    private static final BloomFilterPartitionFactory TEST = new LiteralBloomFilterPartitionFactory(BLOOM_FILTER_FACTORY);

    @BeforeEach
    public void beforeEach() {
        ITEMS.clear();
    }

    @Test
    public void TEST_2() {
        BloomFilter<String> result = TEST.create(5, 2_000_000, 21, 0.5D, 1_000_000);

        Assertions.assertTrue(result.mightContain("estimatedSize"));
        Assertions.assertTrue(result.mightContain("hashingFunctions"));
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
                .put("hashingFunctions", "21")
                .put("falsePositiveRatio", "0.5")
                .put("size", "1000000")
                .build(), ITEMS);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor
    private static final class BloomFilterFactoryMock implements BloomFilterFactory {
        private final Map<String, String> items;

        @Override
        public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
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
