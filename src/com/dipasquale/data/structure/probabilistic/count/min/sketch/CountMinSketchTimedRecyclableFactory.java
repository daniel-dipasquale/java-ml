package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CountMinSketchTimedRecyclableFactory implements CountMinSketchFactory {
    private final CountMinSketchDefaultFactory countMinSketchDefaultFactory;
    private final ExpirationFactory expirationFactory;
    private final RecycledCollector<?> recycledCollector;

    CountMinSketchTimedRecyclableFactory(final MultiFunctionHashing multiFunctionHashing, final ExpirationFactory expirationFactory, final RecycledCollector<?> recycledCollector) {
        this(new CountMinSketchDefaultFactory(multiFunctionHashing), expirationFactory, recycledCollector);
    }

    @Override
    public int getMaximumHashFunctions() {
        return countMinSketchDefaultFactory.getMaximumHashFunctions();
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size, final int bits) {
        ObjectFactory<CountMinSketch<T>> countMinSketchFactory = countMinSketchDefaultFactory.createProxy(estimatedSize, hashFunctions, falsePositiveRatio, size, bits);

        return new CountMinSketchTimedRecyclable<>(countMinSketchFactory, expirationFactory, ensureType(recycledCollector));
    }
}
