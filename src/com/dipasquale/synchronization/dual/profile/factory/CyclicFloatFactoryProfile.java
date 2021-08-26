package com.dipasquale.synchronization.dual.profile.factory;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeCyclicIntegerCounter;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class CyclicFloatFactoryProfile extends AbstractObjectProfile<FloatFactory> {
    @Serial
    private static final long serialVersionUID = 610333221078013332L;
    private final DualModeCyclicIntegerCounter index;

    private CyclicFloatFactoryProfile(final boolean concurrent, final DualModeCyclicIntegerCounter index, final List<Pair<FloatFactory>> floatFactoryPairs) {
        super(concurrent, new DefaultFloatFactory(index, floatFactoryPairs, Pair::getLeft), new DefaultFloatFactory(index, floatFactoryPairs, Pair::getRight));
        this.index = index;
    }

    private CyclicFloatFactoryProfile(final boolean concurrent, final int index, final List<Pair<FloatFactory>> floatFactoryPairs) {
        this(concurrent, new DualModeCyclicIntegerCounter(concurrent, floatFactoryPairs.size(), -1, index), floatFactoryPairs);
    }

    public CyclicFloatFactoryProfile(final boolean concurrent, final Iterable<Pair<FloatFactory>> floatFactoryPairs, final int index) {
        this(concurrent, index, ImmutableList.copyOf(floatFactoryPairs));
    }

    public CyclicFloatFactoryProfile(final boolean concurrent, final Iterable<Pair<FloatFactory>> floatFactoryPairs) {
        this(concurrent, floatFactoryPairs, 0);
    }

    @Override
    protected void ensureProfile(final boolean concurrent) {
        super.ensureProfile(concurrent);
        index.switchMode(concurrent);
    }

    @FunctionalInterface
    private interface FloatFactorySelector extends Serializable {
        FloatFactory get(Pair<FloatFactory> pair);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 2775322329945663278L;
        private final DualModeCyclicIntegerCounter index;
        private final List<Pair<FloatFactory>> floatFactoryPairs;
        private final FloatFactorySelector floatFactorySelector;

        @Override
        public float create() {
            Pair<FloatFactory> floatFactoryPair = floatFactoryPairs.get(index.increment());

            return floatFactorySelector.get(floatFactoryPair).create();
        }
    }
}