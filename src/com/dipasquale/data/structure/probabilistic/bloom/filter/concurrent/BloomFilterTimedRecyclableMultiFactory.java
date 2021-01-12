package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterPartitionFactory;

final class BloomFilterTimedRecyclableMultiFactory implements BloomFilterFactory {
    private final BloomFilterDefaultFactory bloomFilterDefaultFactory;
    private final ExpirySupportProvider expirySupportProvider;
    private final int partitions;

    BloomFilterTimedRecyclableMultiFactory(final BloomFilterDefaultFactory bloomFilterDefaultFactory, final ExpirySupport.Factory expirySupportFactory, final long expiryTime, final int partitions) {
        ArgumentValidator.getInstance().ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidator.getInstance().ensureGreaterThanZero(partitions, "partitions");
        this.bloomFilterDefaultFactory = bloomFilterDefaultFactory;
        this.expirySupportProvider = i -> expirySupportFactory.create(expiryTime * (long) partitions, expiryTime * (long) i);
        this.partitions = partitions;
    }

    BloomFilterTimedRecyclableMultiFactory(final BloomFilterDefaultFactory bloomFilterDefaultFactory, final ExpirySupport expirySupport, final int partitions) {
        ArgumentValidator.getInstance().ensureGreaterThanZero(partitions, "partitions");
        this.bloomFilterDefaultFactory = bloomFilterDefaultFactory;
        this.expirySupportProvider = i -> expirySupport;
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
                ExpirySupport expirySupport = expirySupportProvider.get(index);
                BloomFilterTimedRecyclableFactory bloomFilterFactory = new BloomFilterTimedRecyclableFactory(bloomFilterDefaultFactory, expirySupport);

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
        ExpirySupport get(int index);
    }
}
