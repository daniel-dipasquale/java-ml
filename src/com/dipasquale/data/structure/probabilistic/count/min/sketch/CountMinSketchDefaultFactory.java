package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CountMinSketchDefaultFactory implements CountMinSketchFactory {
    private final MultiHashingFunction multiHashingFunction;

    @Override
    public int getHashingFunctionCount() {
        return multiHashingFunction.getMaximumAllowed();
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
        return new CountMinSketchCasArrayBitManipulator<>(multiHashingFunction, (int) size, hashingFunctionCount, bits);
    }
}
