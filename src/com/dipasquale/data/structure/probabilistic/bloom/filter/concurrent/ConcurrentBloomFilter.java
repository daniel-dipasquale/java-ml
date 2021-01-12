package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterPartitionFactory;

import java.util.UUID;

public final class ConcurrentBloomFilter<T> implements BloomFilter<T> {
    private static final int MAXIMUM_HASH_FUNCTIONS = 256;
    private static final MultiFunctionHashing MULTI_FUNCTION_HASHING = MultiFunctionHashing.createMurmur3_128(MAXIMUM_HASH_FUNCTIONS, UUID.randomUUID().toString());
    private static final BloomFilterDefaultFactory BLOOM_FILTER_DEFAULT_FACTORY = new BloomFilterDefaultFactory(MULTI_FUNCTION_HASHING);
    private static final BloomFilterMultiFactory BLOOM_FILTER_MULTI_FACTORY = new BloomFilterMultiFactory(BloomFilterPartitionFactory.create(BLOOM_FILTER_DEFAULT_FACTORY), 1);
    private final BloomFilter<T> bloomFilter;

    public ConcurrentBloomFilter(final int estimatedSize) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, hashFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio) {
        this.bloomFilter = BLOOM_FILTER_MULTI_FACTORY.createEstimated(estimatedSize, hashFunctions, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final ExpirySupport expirySupport) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirySupport).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final ExpirySupport expirySupport) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirySupport).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions, final ExpirySupport expirySupport) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirySupport).createEstimated(estimatedSize, hashFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final ExpirySupport expirySupport) {
        this.bloomFilter = createMultiTimedRecyclableFactory(expirySupport).createEstimated(estimatedSize, hashFunctions, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final DateTimeSupport dateTimeSupport, final long expiryTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport, expiryTime, partitions).createEstimated(estimatedSize);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expiryTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport, expiryTime, partitions).createEstimated(estimatedSize, falsePositiveRatio);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions, final DateTimeSupport dateTimeSupport, final long expiryTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport, expiryTime, partitions).createEstimated(estimatedSize, hashFunctions);
    }

    public ConcurrentBloomFilter(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final DateTimeSupport dateTimeSupport, final long expiryTime, final int partitions) {
        this.bloomFilter = createMultiTimedRecyclableFactory(dateTimeSupport, expiryTime, partitions).createEstimated(estimatedSize, hashFunctions, falsePositiveRatio);
    }

    private static BloomFilterFactory createMultiTimedRecyclableFactory(final DateTimeSupport dateTimeSupport, final long expiryTime, final int partitions) {
        return new BloomFilterTimedRecyclableMultiFactory(BLOOM_FILTER_DEFAULT_FACTORY, ExpirySupport.createFactory(dateTimeSupport), expiryTime, partitions);
    }

    private static BloomFilterFactory createMultiTimedRecyclableFactory(final ExpirySupport expirySupport) {
        return new BloomFilterTimedRecyclableMultiFactory(BLOOM_FILTER_DEFAULT_FACTORY, expirySupport, 1);
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
