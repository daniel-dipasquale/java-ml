package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ConcurrentBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -5366286836961796106L;
    private static final int MAXIMUM_HASH_FUNCTIONS = 256;
    private final MultiHashingFunction multiHashingFunction;

    public ConcurrentBloomFilterFactory() {
        this(null);
    }

    @Override
    public int getHashingFunctionCount() {
        if (multiHashingFunction == null) {
            return MAXIMUM_HASH_FUNCTIONS;
        }

        return multiHashingFunction.getMaximumAllowed();
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        if (multiHashingFunction == null) {
            return new SynchronizedGuavaBloomFilter<>(estimatedSize, falsePositiveRatio);
        }

        return new AtomicLongArrayBloomFilter<>(multiHashingFunction, (int) size, hashingFunctionCount);
    }
}
