package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.probabilistic.BitArrayCalculator;

import java.io.Serial;
import java.io.Serializable;

public final class MultiPartBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 3589002588701135980L;
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int count;

    public MultiPartBloomFilterFactory(final BloomFilterPartitionFactory bloomFilterPartitionFactory, final int count) {
        ArgumentValidatorSupport.ensureGreaterThanZero(count, "count");
        this.bloomFilterPartitionFactory = bloomFilterPartitionFactory;
        this.count = count;
    }

    @Override
    public int getHashingFunctionCount() {
        return bloomFilterPartitionFactory.getHashingFunctionCount();
    }

    private BloomFilterPartitionFactoryProxy createPartitionFactoryProxy(final BitArrayCalculator.Result readjusted, final int hashingFunctionCount, final double falsePositiveRatio) {
        return new DefaultBloomFilterPartitionFactoryProxy(bloomFilterPartitionFactory, readjusted.getEstimatedSize(), hashingFunctionCount, falsePositiveRatio, readjusted.getSize());
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        BitArrayCalculator.Result readjusted = BitArrayCalculator.readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return bloomFilterPartitionFactory.create(0, readjusted.getEstimatedSize(), hashingFunctionCount, falsePositiveRatio, readjusted.getSize());
        }

        BloomFilterPartitionFactoryProxy bloomFilterPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashingFunctionCount, falsePositiveRatio);

        return new MultiPartBloomFilter<>(bloomFilterPartitionFactoryProxy, readjusted.getCount());
    }
}
