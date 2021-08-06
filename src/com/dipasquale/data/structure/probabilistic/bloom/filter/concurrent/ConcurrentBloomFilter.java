package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.DefaultMultiHashingFunction;
import com.dipasquale.data.structure.probabilistic.HashingFunctionAlgorithm;
import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.DefaultBloomFilterPartitionFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.MultiPartBloomFilterFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public final class ConcurrentBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -3477491277835878755L;
    private static final int MAXIMUM_HASH_FUNCTIONS = 256;
    private static final MultiHashingFunction MULTI_FUNCTION_HASHING = new DefaultMultiHashingFunction(MAXIMUM_HASH_FUNCTIONS, HashingFunctionAlgorithm.MURMUR_3_128, UUID.randomUUID().toString());
    private static final ConcurrentBloomFilterFactory BLOOM_FILTER_DEFAULT_FACTORY = new ConcurrentBloomFilterFactory(MULTI_FUNCTION_HASHING);
    private static final MultiPartBloomFilterFactory BLOOM_FILTER_MULTI_FACTORY = new MultiPartBloomFilterFactory(new DefaultBloomFilterPartitionFactory(BLOOM_FILTER_DEFAULT_FACTORY), 1);
    private final BloomFilter<T> bloomFilter;

    public ConcurrentBloomFilter(final int estimatedSize) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, hashingFunctionCount);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, hashingFunctionCount, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirationFactory).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirationFactory).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirationFactory).createEstimated(estimatedSize, hashingFunctionCount);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final ExpirationFactory expirationFactory) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirationFactory).createEstimated(estimatedSize, hashingFunctionCount, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport::createBucketExpirationFactory, expirationTime, partitions).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport::createBucketExpirationFactory, expirationTime, partitions).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport::createBucketExpirationFactory, expirationTime, partitions).createEstimated(estimatedSize, hashingFunctionCount);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expirationTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport::createBucketExpirationFactory, expirationTime, partitions).createEstimated(estimatedSize, hashingFunctionCount, falsePositiveRatio);
    }

    private static BloomFilterFactory createMultiTimedRecyclableFactory(final ExpirationFactory.Creator expirationFactoryCreator, final long expirationTime, final int partitions) {
        return new MultiPartRecyclableBloomFilterFactory(BLOOM_FILTER_DEFAULT_FACTORY, expirationFactoryCreator, expirationTime, partitions);
    }

    private static BloomFilterFactory createMultiTimedRecyclableFactory(final ExpirationFactory expirationFactory) {
        return new MultiPartRecyclableBloomFilterFactory(BLOOM_FILTER_DEFAULT_FACTORY, expirationFactory, 1);
    }

    @Override
    public boolean mightContain(final T item) {
        return bloomFilter.mightContain(item);
    }

    @Override
    public boolean add(final T item) {
        return bloomFilter.add(item);
    }
}
