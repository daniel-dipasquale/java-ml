/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.probabilistic.BitArrayCalculator;

import java.io.Serial;
import java.io.Serializable;

public final class MultiPartCountMinSketchFactory implements CountMinSketchFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 1346700301439475546L;
    private final CountMinSketchPartitionFactory countMinSketchPartitionFactory;
    private final int count;

    public MultiPartCountMinSketchFactory(final CountMinSketchPartitionFactory countMinSketchPartitionFactory, final int count) {
        ArgumentValidatorSupport.ensureGreaterThanZero(count, "count");
        this.countMinSketchPartitionFactory = countMinSketchPartitionFactory;
        this.count = count;
    }

    private CountMinSketchPartitionFactory.Proxy createPartitionFactoryProxy(final BitArrayCalculator.Result readjusted, final int hashingFunctions, final double falsePositiveRatio, final int bits) {
        return new DefaultCountMinSketchPartitionFactoryProxy(countMinSketchPartitionFactory, readjusted.getEstimatedSize(), hashingFunctions, falsePositiveRatio, readjusted.getSize(), bits);
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        BitArrayCalculator.Result readjusted = BitArrayCalculator.readjust(count, estimatedSize, size);

        if (readjusted.getCount() == 1) {
            return countMinSketchPartitionFactory.create(0, readjusted.getEstimatedSize(), hashingFunctions, falsePositiveRatio, readjusted.getSize(), bitsForCounter);
        }

        CountMinSketchPartitionFactory.Proxy countMinSketchPartitionFactoryProxy = createPartitionFactoryProxy(readjusted, hashingFunctions, falsePositiveRatio, bitsForCounter);

        return new MultiPartCountMinSketch<>(countMinSketchPartitionFactoryProxy, readjusted.getCount());
    }
}
