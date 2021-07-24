package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.time.ExpirationFactory;

final class BloomFilterTimedRecyclableMultiFactory implements BloomFilterFactory {
    private final BloomFilterDefaultFactory bloomFilterDefaultFactory;
    private final ExpirySupportProvider expirySupportProvider;
    private final int partitions;

    BloomFilterTimedRecyclableMultiFactory(final BloomFilterDefaultFactory bloomFilterDefaultFactory, final ExpirationFactory.Creator expirationFactoryCreator, final long expiryTime, final int partitions) {
        ArgumentValidatorSupport.ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidatorSupport.ensureGreaterThanZero(partitions, "partitions");
        this.bloomFilterDefaultFactory = bloomFilterDefaultFactory;
        this.expirySupportProvider = i -> expirationFactoryCreator.create(expiryTime * (long) partitions, expiryTime * (long) i);
        this.partitions = partitions;
    }

    BloomFilterTimedRecyclableMultiFactory(final BloomFilterDefaultFactory bloomFilterDefaultFactory, final ExpirationFactory expirationFactory, final int partitions) {
        ArgumentValidatorSupport.ensureGreaterThanZero(partitions, "partitions");
        this.bloomFilterDefaultFactory = bloomFilterDefaultFactory;
        this.expirySupportProvider = i -> expirationFactory;
        this.partitions = partitions;
    }

    @Override
    public int getMaximumHashFunctions() {
        return bloomFilterDefaultFactory.getMaximumHashFunctions();
    }

    private BloomFilterPartitionFactory createBloomFilterPartitionFactory() {
        return new BloomFilterPartitionFactory() {
            @Override
            public int getMaximumHashFunctions() {
                return bloomFilterDefaultFactory.getMaximumHashFunctions();
            }

            @Override
            public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
                ExpirationFactory expirationFactory = expirySupportProvider.get(index);
                BloomFilterTimedRecyclableFactory bloomFilterFactory = new BloomFilterTimedRecyclableFactory(bloomFilterDefaultFactory, expirationFactory);

                return bloomFilterFactory.create(estimatedSize, hashFunctions, falsePositiveRatio, size);
            }
        };
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        BloomFilterPartitionFactory bloomFilterPartitionFactory = createBloomFilterPartitionFactory();
        BloomFilterMultiFactory bloomFilterMultiFactory = new BloomFilterMultiFactory(bloomFilterPartitionFactory, partitions);

        return bloomFilterMultiFactory.create(estimatedSize, hashFunctions, falsePositiveRatio, size);
    }

    @FunctionalInterface
    interface ExpirySupportProvider {
        ExpirationFactory get(int index);
    }
}
