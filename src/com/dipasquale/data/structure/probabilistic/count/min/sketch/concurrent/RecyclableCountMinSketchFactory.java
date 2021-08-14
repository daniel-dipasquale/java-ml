package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketchFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecyclableCountMinSketchFactory implements CountMinSketchFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2266138661340338391L;
    private final DefaultCountMinSketchFactory defaultCountMinSketchFactory;
    private final ExpirationFactory expirationFactory;
    private final RecycledCountMinSketchCollector<?> recycledCollector;

    public RecyclableCountMinSketchFactory(final HashingFunction hashingFunction, final ExpirationFactory expirationFactory, final RecycledCountMinSketchCollector<?> recycledCollector) {
        this(new DefaultCountMinSketchFactory(hashingFunction), expirationFactory, recycledCollector);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        ObjectFactory<CountMinSketch<T>> countMinSketchFactory = defaultCountMinSketchFactory.createProxy(estimatedSize, hashingFunctions, falsePositiveRatio, size, bitsForCounter);

        return new RecyclableCountMinSketch<>(countMinSketchFactory, expirationFactory, ensureType(recycledCollector));
    }
}
