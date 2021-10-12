package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.data.structure.probabilistic.BitArrayCalculator;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MultiPartBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 2407535984977128416L;
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int count;

    private BloomFilterPartitionFactory.Proxy createPartitionFactoryProxy(final BitArrayCalculator.Result readjusted, final int hashingFunctions, final double falsePositiveRatio) {
        return new DefaultBloomFilterPartitionFactoryProxy(bloomFilterPartitionFactory, readjusted.getEstimatedSize(), hashingFunctions, falsePositiveRatio, readjusted.getSize());
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
        BitArrayCalculator.Result readjusted = BitArrayCalculator.readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return bloomFilterPartitionFactory.create(0, readjusted.getEstimatedSize(), hashingFunctions, falsePositiveRatio, readjusted.getSize());
        }

        BloomFilterPartitionFactory.Proxy bloomFilterPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashingFunctions, falsePositiveRatio);

        return new MultiPartBloomFilter<>(bloomFilterPartitionFactoryProxy, readjusted.getCount());
    }
}
