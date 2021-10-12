package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketchFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class AtomicLongArrayCountMinSketchFactory implements CountMinSketchFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3669793887819190074L;
    private final HashingFunction hashingFunction;

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        return new AtomicLongArrayCountMinSketch<>(hashingFunction, (int) size, hashingFunctions, bitsForCounter);
    }
}
