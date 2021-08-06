package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterPartitionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.MultiPartBloomFilterFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class MultiPartRecyclableBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 1320110514729455374L;
    private final ConcurrentBloomFilterFactory concurrentBloomFilterFactory;
    private final ExpirationSupportProvider expirationSupportProvider;
    private final int partitions;

    public MultiPartRecyclableBloomFilterFactory(final ConcurrentBloomFilterFactory concurrentBloomFilterFactory, final ExpirationFactory.Creator expirationFactoryCreator, final long expirationTime, final int partitions) {
        ArgumentValidatorSupport.ensureGreaterThanZero(expirationTime, "expirationTime");
        ArgumentValidatorSupport.ensureGreaterThanZero(partitions, "partitions");
        this.concurrentBloomFilterFactory = concurrentBloomFilterFactory;
        this.expirationSupportProvider = new StaggeringExpirationSupportProvider(expirationFactoryCreator, expirationTime, partitions);
        this.partitions = partitions;
    }

    public MultiPartRecyclableBloomFilterFactory(final ConcurrentBloomFilterFactory concurrentBloomFilterFactory, final ExpirationFactory expirationFactory, final int partitions) {
        ArgumentValidatorSupport.ensureGreaterThanZero(partitions, "partitions");
        this.concurrentBloomFilterFactory = concurrentBloomFilterFactory;
        this.expirationSupportProvider = new LiteralExpirationSupportProvider(expirationFactory);
        this.partitions = partitions;
    }

    @Override
    public int getHashingFunctionCount() {
        return concurrentBloomFilterFactory.getHashingFunctionCount();
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        BloomFilterPartitionFactory bloomFilterPartitionFactory = new DefaultBloomFilterPartitionFactory(concurrentBloomFilterFactory, expirationSupportProvider);
        MultiPartBloomFilterFactory multiPartBloomFilterFactory = new MultiPartBloomFilterFactory(bloomFilterPartitionFactory, partitions);

        return multiPartBloomFilterFactory.create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }

    @FunctionalInterface
    private interface ExpirationSupportProvider {
        ExpirationFactory get(int index);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StaggeringExpirationSupportProvider implements ExpirationSupportProvider, Serializable {
        @Serial
        private static final long serialVersionUID = 57468981676574430L;
        private final ExpirationFactory.Creator expirationFactoryCreator;
        private final long expirationTime;
        private final long partitions;

        @Override
        public ExpirationFactory get(final int index) {
            return expirationFactoryCreator.create(expirationTime * partitions, expirationTime * (long) index);
        }
    }

    @RequiredArgsConstructor
    private static final class LiteralExpirationSupportProvider implements ExpirationSupportProvider, Serializable {
        @Serial
        private static final long serialVersionUID = -9216489389352943448L;
        private final ExpirationFactory expirationFactory;

        @Override
        public ExpirationFactory get(final int index) {
            return expirationFactory;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultBloomFilterPartitionFactory implements BloomFilterPartitionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 9070207243776926309L;
        private final ConcurrentBloomFilterFactory concurrentBloomFilterFactory;
        private final ExpirationSupportProvider expirationSupportProvider;

        @Override
        public int getHashingFunctionCount() {
            return concurrentBloomFilterFactory.getHashingFunctionCount();
        }

        @Override
        public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
            ExpirationFactory expirationFactory = expirationSupportProvider.get(index);
            RecyclableBloomFilterFactory bloomFilterFactory = new RecyclableBloomFilterFactory(concurrentBloomFilterFactory, expirationFactory);

            return bloomFilterFactory.create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
        }
    }
}
