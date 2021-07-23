package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.probabilistic.DataStructureMultiCalculator;

final class BloomFilterMultiFactory implements BloomFilterFactory {
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int count;

    BloomFilterMultiFactory(final BloomFilterPartitionFactory bloomFilterPartitionFactory, final int count) {
        ArgumentValidatorSupport.ensureGreaterThanZero(count, "count");
        this.bloomFilterPartitionFactory = bloomFilterPartitionFactory;
        this.count = count;
    }

    @Override
    public int getMaximumHashFunctions() {
        return bloomFilterPartitionFactory.getMaximumHashFunctions();
    }

    private BloomFilterPartitionFactory.Proxy createPartitionFactoryProxy(final DataStructureMultiCalculator.Result readjusted, final int hashFunctions, final double falsePositiveRatio) {
        return BloomFilterPartitionFactory.Proxy.create(bloomFilterPartitionFactory, readjusted.getEstimatedSize(), hashFunctions, falsePositiveRatio, readjusted.getSize());
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        DataStructureMultiCalculator.Result readjusted = DataStructureMultiCalculator.getInstance().readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return bloomFilterPartitionFactory.create(0, readjusted.getEstimatedSize(), hashFunctions, falsePositiveRatio, readjusted.getSize());
        }

        BloomFilterPartitionFactory.Proxy bloomFilterPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashFunctions, falsePositiveRatio);

        return new BloomFilterMulti<>(bloomFilterPartitionFactoryProxy, readjusted.getCount());
    }
}
