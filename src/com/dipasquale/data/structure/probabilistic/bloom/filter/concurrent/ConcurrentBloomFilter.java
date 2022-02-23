package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationFactoryProvider;
import com.dipasquale.common.time.LiteralExpirationFactoryProvider;
import com.dipasquale.common.time.StaggeringExpirationFactoryProvider;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.HashingFunctionAlgorithm;
import com.dipasquale.data.structure.probabilistic.HashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.JdkHashingFunctionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.MultiPartBloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.StrategyBloomFilterPartitionFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class ConcurrentBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -3477491277835878755L;
    private static final HashingFunctionFactory HASHING_FUNCTION_FACTORY = new JdkHashingFunctionFactory();
    private static final HashingFunction HASHING_FUNCTION = HASHING_FUNCTION_FACTORY.create(HashingFunctionAlgorithm.SHA_1, ConcurrentBloomFilter.class.getSimpleName());
    private static final AtomicLongArrayBloomFilterFactory DEFAULT_BLOOM_FILTER_FACTORY = new AtomicLongArrayBloomFilterFactory(HASHING_FUNCTION);
    private static final MultiPartBloomFilterFactory MULTI_PART_BLOOM_FILTER_FACTORY = new MultiPartBloomFilterFactory(new StrategyBloomFilterPartitionFactory(DEFAULT_BLOOM_FILTER_FACTORY), 1);
    private final BloomFilter<T> bloomFilter;

    public ConcurrentBloomFilter(final int estimatedSize) {
        this.bloomFilter = MULTI_PART_BLOOM_FILTER_FACTORY.createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio) {
        this.bloomFilter = MULTI_PART_BLOOM_FILTER_FACTORY.createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions) {
        this.bloomFilter = MULTI_PART_BLOOM_FILTER_FACTORY.createEstimated(estimatedSize, hashingFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio) {
        this.bloomFilter = MULTI_PART_BLOOM_FILTER_FACTORY.createEstimated(estimatedSize, hashingFunctions, falsePositiveRatio);
    }

    private static BloomFilterFactory createMultiPartRecyclableFactory(final ExpirationFactory expirationFactory) {
        return new MultiPartRecyclableBloomFilterFactory(DEFAULT_BLOOM_FILTER_FACTORY, new LiteralExpirationFactoryProvider(expirationFactory));
    }

    public ConcurrentBloomFilter(final int estimatedSize, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiPartRecyclableFactory(expirationFactory).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiPartRecyclableFactory(expirationFactory).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiPartRecyclableFactory(expirationFactory).createEstimated(estimatedSize, hashingFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiPartRecyclableFactory(expirationFactory).createEstimated(estimatedSize, hashingFunctions, falsePositiveRatio);
    }

    private static BloomFilterFactory createMultiPartStaggeringRecyclableFactory(final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        InternalExpirationFactoryCreator internalExpirationFactoryCreator = new InternalExpirationFactoryCreator(dateTimeSupport);
        ExpirationFactoryProvider expirationFactoryProvider = new StaggeringExpirationFactoryProvider(internalExpirationFactoryCreator, expirationTime, partitions);

        return new MultiPartRecyclableBloomFilterFactory(DEFAULT_BLOOM_FILTER_FACTORY, expirationFactoryProvider);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiPartStaggeringRecyclableFactory(dateTimeSupport, expirationTime, partitions).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiPartStaggeringRecyclableFactory(dateTimeSupport, expirationTime, partitions).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiPartStaggeringRecyclableFactory(dateTimeSupport, expirationTime, partitions).createEstimated(estimatedSize, hashingFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiPartStaggeringRecyclableFactory(dateTimeSupport, expirationTime, partitions).createEstimated(estimatedSize, hashingFunctions, falsePositiveRatio);
    }

    @Override
    public boolean mightContain(final T item) {
        return bloomFilter.mightContain(item);
    }

    @Override
    public boolean add(final T item) {
        return bloomFilter.add(item);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalExpirationFactoryCreator implements ExpirationFactory.Creator, Serializable {
        @Serial
        private static final long serialVersionUID = 6504012671008909093L;
        private final DateTimeSupport dateTimeSupport;

        @Override
        public ExpirationFactory create(final long expirationTime, final long offset) {
            return dateTimeSupport.createBucketExpirationFactory(expirationTime, offset);
        }
    }
}
