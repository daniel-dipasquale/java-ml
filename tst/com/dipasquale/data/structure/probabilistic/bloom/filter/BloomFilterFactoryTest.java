package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public final class BloomFilterFactoryTest {
    private static final Map<String, String> ITEMS = new HashMap<>();
    private static final BloomFilterFactory TEST = new BloomFilterFactoryMock(ITEMS);

    @BeforeEach
    public void beforeEach() {
        ITEMS.clear();
    }

    @Test
    public void TEST_2() {
        BloomFilter<String> result = TEST.createEstimated(2_000_000);

        Assertions.assertTrue(result.mightContain("estimatedSize"));
        Assertions.assertTrue(result.mightContain("hashingFunctions"));
        Assertions.assertTrue(result.mightContain("falsePositiveRatio"));
        Assertions.assertTrue(result.mightContain("size"));
        Assertions.assertFalse(result.add("size"));
        Assertions.assertTrue(result.add("does-not-exist"));
    }

    @Test
    public void TEST_3() {
        TEST.createEstimated(2_000_000);

        Assertions.assertEquals(Map.ofEntries(
                Map.entry("estimatedSize", "2000000"),
                Map.entry("hashingFunctions", "21"),
                Map.entry("falsePositiveRatio", "4.99999750000125E-7"),
                Map.entry("size", "943684")
        ), ITEMS);
    }

    @Test
    public void TEST_4() {
        TEST.createEstimated(2_000_000, 0.5D);

        Assertions.assertEquals(Map.ofEntries(
                Map.entry("estimatedSize", "2000000"),
                Map.entry("hashingFunctions", "1"),
                Map.entry("falsePositiveRatio", "0.5"),
                Map.entry("size", "45085")
        ), ITEMS);
    }

    @Test
    public void TEST_5() {
        TEST.createEstimated(2_000_000, 8);

        Assertions.assertEquals(Map.ofEntries(
                Map.entry("estimatedSize", "2000000"),
                Map.entry("hashingFunctions", "8"),
                Map.entry("falsePositiveRatio", "4.99999750000125E-7"),
                Map.entry("size", "1404388")
        ), ITEMS);
    }

    @Test
    public void TEST_6() {
        TEST.createEstimated(2_000_000, 8, 0.5D);

        Assertions.assertEquals(Map.ofEntries(
                Map.entry("estimatedSize", "2000000"),
                Map.entry("hashingFunctions", "8"),
                Map.entry("falsePositiveRatio", "0.5"),
                Map.entry("size", "100444")
        ), ITEMS);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
