package com.dipasquale.common.switcher.factory;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class CyclicFloatFactorySwitcher extends AbstractObjectSwitcher<FloatFactory> {
    @Serial
    private static final long serialVersionUID = 610333221078013332L;

    private CyclicFloatFactorySwitcher(final boolean isOn, final List<Pair<FloatFactory>> floatFactoryPairs, final AtomicInteger index) {
        super(isOn, new DefaultFloatFactory(index, floatFactoryPairs, Pair::getLeft), new DefaultFloatFactory(index, floatFactoryPairs, Pair::getRight));
    }

    public CyclicFloatFactorySwitcher(final boolean isOn, final Iterable<Pair<FloatFactory>> floatFactoryPairs, final int index) {
        this(isOn, ImmutableList.copyOf(floatFactoryPairs), new AtomicInteger(index));
    }

    public CyclicFloatFactorySwitcher(final boolean isOn, final Iterable<Pair<FloatFactory>> floatFactoryPairs) {
        this(isOn, floatFactoryPairs, 0);
    }

    private static float create(final AtomicInteger index, final List<Pair<FloatFactory>> floatFactoryPairs, final FloatFactoryAccessor accessor) {
        int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % floatFactoryPairs.size());
        Pair<FloatFactory> floatFactoryPair = floatFactoryPairs.get(indexFixed);

        return accessor.get(floatFactoryPair).create();
    }

    @FunctionalInterface
    private interface FloatFactoryAccessor extends Serializable {
        FloatFactory get(Pair<FloatFactory> pair);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 2775322329945663278L;
        private final AtomicInteger index;
        private final List<Pair<FloatFactory>> floatFactoryPairs;
        private final FloatFactoryAccessor floatFactoryAccessor;

        @Override
        public float create() {
            return CyclicFloatFactorySwitcher.create(index, floatFactoryPairs, floatFactoryAccessor);
        }
    }
}