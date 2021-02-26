package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CountMinSketchDefaultFactory implements CountMinSketchFactory {
    private final MultiFunctionHashing multiFunctionHashing;

    @Override
    public int getMaximumHashFunctions() {
        return multiFunctionHashing.getMaximumHashFunctions();
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size, final int bits) {
        return new CountMinSketchCasArrayBitManipulator<>(multiFunctionHashing, (int) size, hashFunctions, bits);
    }
}
