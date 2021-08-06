package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.probabilistic.BitArrayCalculator;

final class CountMinSketchMultiFactory implements CountMinSketchFactory {
    private final CountMinSketchPartitionFactory countMinSketchPartitionFactory;
    private final int count;

    CountMinSketchMultiFactory(final CountMinSketchPartitionFactory countMinSketchPartitionFactory, final int count) {
        ArgumentValidatorSupport.ensureGreaterThanZero(count, "count");
        this.countMinSketchPartitionFactory = countMinSketchPartitionFactory;
        this.count = count;
    }

    @Override
    public int getHashingFunctionCount() {
        return countMinSketchPartitionFactory.getHashingFunctionCount();
    }

    private CountMinSketchPartitionFactory.Proxy createPartitionFactoryProxy(final BitArrayCalculator.Result readjusted, final int hashingFunctionCount, final double falsePositiveRatio, final int bits) {
        return CountMinSketchPartitionFactory.Proxy.create(countMinSketchPartitionFactory, readjusted.getEstimatedSize(), hashingFunctionCount, falsePositiveRatio, readjusted.getSize(), bits);
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
        BitArrayCalculator.Result readjusted = BitArrayCalculator.readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return countMinSketchPartitionFactory.create(0, readjusted.getEstimatedSize(), hashingFunctionCount, falsePositiveRatio, readjusted.getSize(), bits);
        }

        CountMinSketchPartitionFactory.Proxy countMinSketchPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashingFunctionCount, falsePositiveRatio, bits);

        return new CountMinSketchMulti<>(countMinSketchPartitionFactoryProxy, readjusted.getCount());
    }
}
