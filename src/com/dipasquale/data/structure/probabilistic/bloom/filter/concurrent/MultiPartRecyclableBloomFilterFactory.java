package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationFactoryProvider;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterPartitionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.MultiPartBloomFilterFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MultiPartRecyclableBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -1081831773628695587L;
    private final AtomicLongArrayBloomFilterFactory atomicLongArrayBloomFilterFactory;
    private final ExpirationFactoryProvider expirationFactoryProvider;

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
        BloomFilterPartitionFactory bloomFilterPartitionFactory = new InternalBloomFilterPartitionFactory(atomicLongArrayBloomFilterFactory, expirationFactoryProvider);
        MultiPartBloomFilterFactory multiPartBloomFilterFactory = new MultiPartBloomFilterFactory(bloomFilterPartitionFactory, expirationFactoryProvider.size());

        return multiPartBloomFilterFactory.create(estimatedSize, hashingFunctions, falsePositiveRatio, size);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalBloomFilterPartitionFactory implements BloomFilterPartitionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 8335206399723473140L;
        private final AtomicLongArrayBloomFilterFactory atomicLongArrayBloomFilterFactory;
        private final ExpirationFactoryProvider expirationFactoryProvider;

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
            ExpirationFactory expirationFactory = expirationFactoryProvider.get(index);
            RecyclableBloomFilterFactory recyclableBloomFilterFactory = new RecyclableBloomFilterFactory(atomicLongArrayBloomFilterFactory, expirationFactory);

            return recyclableBloomFilterFactory.create(estimatedSize, hashingFunctions, falsePositiveRatio, size);
        }
    }
}
