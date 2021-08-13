/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LiteralCountMinSketchPartitionFactory implements CountMinSketchPartitionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2183884097024152126L;
    private final CountMinSketchFactory countMinSketchFactory;

    @Override
    public <T> CountMinSketch<T> create(final int index, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        return countMinSketchFactory.create(estimatedSize, hashingFunctions, falsePositiveRatio, size, bitsForCounter);
    }
}
