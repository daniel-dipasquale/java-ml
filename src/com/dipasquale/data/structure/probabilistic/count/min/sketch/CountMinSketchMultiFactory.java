package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.data.structure.probabilistic.DataStructureMultiCalculator;

final class CountMinSketchMultiFactory implements CountMinSketchFactory {
    private final CountMinSketchPartitionFactory countMinSketchPartitionFactory;
    private final int count;

    CountMinSketchMultiFactory(final CountMinSketchPartitionFactory countMinSketchPartitionFactory, final int count) {
        ArgumentValidator.ensureGreaterThanZero(count, "count");
        this.countMinSketchPartitionFactory = countMinSketchPartitionFactory;
        this.count = count;
    }

    @Override
    public int getMaximumHashFunctions() {
        return countMinSketchPartitionFactory.getMaximumHashFunctions();
    }

    private CountMinSketchPartitionFactory.Proxy createPartitionFactoryProxy(final DataStructureMultiCalculator.Result readjusted, final int hashFunctions, final double falsePositiveRatio, final int bits) {
        return CountMinSketchPartitionFactory.Proxy.create(countMinSketchPartitionFactory, readjusted.getEstimatedSize(), hashFunctions, falsePositiveRatio, readjusted.getSize(), bits);
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size, final int bits) {
        DataStructureMultiCalculator.Result readjusted = DataStructureMultiCalculator.getInstance().readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return countMinSketchPartitionFactory.create(0, readjusted.getEstimatedSize(), hashFunctions, falsePositiveRatio, readjusted.getSize(), bits);
        }

        CountMinSketchPartitionFactory.Proxy countMinSketchPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashFunctions, falsePositiveRatio, bits);

        return new CountMinSketchMulti<>(countMinSketchPartitionFactoryProxy, readjusted.getCount());
    }
}
