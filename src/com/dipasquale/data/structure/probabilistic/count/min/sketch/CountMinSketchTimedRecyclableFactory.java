package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CountMinSketchTimedRecyclableFactory implements CountMinSketchFactory {
    private final CountMinSketchDefaultFactory countMinSketchDefaultFactory;
    private final ExpirationFactory expirationFactory;
    private final RecycledCollector<?> recycledCollector;

    CountMinSketchTimedRecyclableFactory(final MultiHashingFunction multiHashingFunction, final ExpirationFactory expirationFactory, final RecycledCollector<?> recycledCollector) {
        this(new CountMinSketchDefaultFactory(multiHashingFunction), expirationFactory, recycledCollector);
    }

    @Override
    public int getHashingFunctionCount() {
        return countMinSketchDefaultFactory.getHashingFunctionCount();
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
        ObjectFactory<CountMinSketch<T>> countMinSketchFactory = countMinSketchDefaultFactory.createProxy(estimatedSize, hashingFunctionCount, falsePositiveRatio, size, bits);

        return new CountMinSketchTimedRecyclable<>(countMinSketchFactory, expirationFactory, ensureType(recycledCollector));
    }
}
