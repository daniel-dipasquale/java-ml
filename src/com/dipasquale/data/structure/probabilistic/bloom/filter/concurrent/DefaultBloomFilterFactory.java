/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DefaultBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -5366286836961796106L;
    private final HashingFunction hashingFunction;

    public DefaultBloomFilterFactory() {
        this(null);
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
        if (hashingFunction == null) {
            return new SynchronizedGuavaBloomFilter<>(estimatedSize, falsePositiveRatio);
        }

        return new AtomicLongArrayBloomFilter<>(hashingFunction, (int) size, hashingFunctions);
    }
}
