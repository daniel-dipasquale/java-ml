/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.probabilistic.BitArrayCalculator;

import java.io.Serial;
import java.io.Serializable;

public final class MultiPartBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 2407535984977128416L;
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int count;

    public MultiPartBloomFilterFactory(final BloomFilterPartitionFactory bloomFilterPartitionFactory, final int count) {
        ArgumentValidatorSupport.ensureGreaterThanZero(count, "count");
        this.bloomFilterPartitionFactory = bloomFilterPartitionFactory;
        this.count = count;
    }

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
