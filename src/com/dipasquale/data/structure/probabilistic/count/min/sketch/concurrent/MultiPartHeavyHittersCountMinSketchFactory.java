package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationFactoryProvider;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketchFactory;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketchPartitionFactory;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.MultiPartCountMinSketchFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiPartHeavyHittersCountMinSketchFactory implements CountMinSketchFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 6074021532417328154L;
    private final DefaultCountMinSketchFactory defaultCountMinSketchFactory;
    private final ExpirationFactoryProvider expirationFactoryProvider;
    private final HeavyHittersCountMinSketchCollector<?> heavyHittersCollector;
    private final int topLimit;

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        CountMinSketchPartitionFactory countMinSketchPartitionFactory = new InternalCountMinSketchPartitionFactory(defaultCountMinSketchFactory, expirationFactoryProvider, heavyHittersCollector, topLimit);
        MultiPartCountMinSketchFactory multiPartCountMinSketchFactory = new MultiPartCountMinSketchFactory(countMinSketchPartitionFactory, expirationFactoryProvider.size());

        return multiPartCountMinSketchFactory.create(estimatedSize, hashingFunctions, falsePositiveRatio, size, bitsForCounter);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalCountMinSketchPartitionFactory implements CountMinSketchPartitionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -6755576789843076419L;
        private final DefaultCountMinSketchFactory defaultCountMinSketchFactory;
        private final ExpirationFactoryProvider expirationFactoryProvider;
        private final HeavyHittersCountMinSketchCollector<?> heavyHittersCollector;
        private final int topLimit;

        @Override
        public <T> CountMinSketch<T> create(final int index, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
            ObjectFactory<CountMinSketch<T>> countMinSketchFactory = defaultCountMinSketchFactory.createProxy(estimatedSize, hashingFunctions, falsePositiveRatio, size, bitsForCounter);
            ExpirationFactory expirationFactory = expirationFactoryProvider.get(index);

            return new HeavyHittersCountMinSketch<>(countMinSketchFactory, expirationFactory, ensureType(heavyHittersCollector), topLimit);
        }
    }
}
