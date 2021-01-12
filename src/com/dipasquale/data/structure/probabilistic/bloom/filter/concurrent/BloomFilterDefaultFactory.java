package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BloomFilterDefaultFactory implements BloomFilterFactory {
    private static final int MAXIMUM_HASH_FUNCTIONS = 256;
    private final MultiFunctionHashing multiFunctionHashing;

    BloomFilterDefaultFactory() {
        this(null);
    }

    @Override
    public int getMaximumHashFunctions() {
        if (multiFunctionHashing == null) {
            return MAXIMUM_HASH_FUNCTIONS;
        }

        return multiFunctionHashing.getMaximumHashFunctions();
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        if (multiFunctionHashing == null) {
            return new BloomFilterGuava<>(estimatedSize, falsePositiveRatio);
        }

        return new BloomFilterCasArrayBitManipulator<>(multiFunctionHashing, (int) size, hashFunctions);
    }
}
