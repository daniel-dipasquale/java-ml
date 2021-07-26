package com.dipasquale.common.switcher;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class CyclicFloatFactorySwitcher extends AbstractObjectSwitcher<FloatFactory> {
    @Serial
    private static final long serialVersionUID = 610333221078013332L;
    private final List<Pair<FloatFactory>> floatFactoryPairs;
    private final AtomicInteger index;
    @Getter(AccessLevel.PROTECTED)
    private final FloatFactory on;
    @Getter(AccessLevel.PROTECTED)
    private final FloatFactory off;

    public CyclicFloatFactorySwitcher(final boolean isOn, final Iterable<Pair<FloatFactory>> floatFactoryPairs, final int index) {
        super(isOn);
        this.floatFactoryPairs = ImmutableList.copyOf(floatFactoryPairs);
        this.index = new AtomicInteger(index);
        this.on = new OnFloatFactory();
        this.off = new OffFloatFactory();
    }

    public CyclicFloatFactorySwitcher(final boolean isOn, final Iterable<Pair<FloatFactory>> floatFactoryPairs) {
        this(isOn, floatFactoryPairs, 0);
    }

    private float create(final FloatFactoryAccessor accessor) {
        int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % floatFactoryPairs.size());
        Pair<FloatFactory> floatFactoryPair = floatFactoryPairs.get(indexFixed);

        return accessor.get(floatFactoryPair).create();
    }

    @FunctionalInterface
    private interface FloatFactoryAccessor {
        FloatFactory get(Pair<FloatFactory> pair);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OnFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 2775322329945663278L;

        @Override
        public float create() {
            return CyclicFloatFactorySwitcher.this.create(Pair::getLeft);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OffFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 5863432999928215080L;

        @Override
        public float create() {
            return CyclicFloatFactorySwitcher.this.create(Pair::getRight);
        }
    }
}