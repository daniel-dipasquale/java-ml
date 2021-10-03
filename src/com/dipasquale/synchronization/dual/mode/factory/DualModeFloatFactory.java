package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeFloatFactory implements FloatFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6491310596371883595L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final FloatFactory concurrentFloatFactory;
    private final FloatFactory defaultFloatFactory;

    public DualModeFloatFactory(final int concurrencyLevel, final FloatFactory concurrentFloatFactory, final FloatFactory defaultFloatFactory) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentFloatFactory = concurrentFloatFactory;
        this.defaultFloatFactory = defaultFloatFactory;
    }

    public DualModeFloatFactory(final int concurrencyLevel, final Pair<FloatFactory> floatFactoryPair) {
        this(concurrencyLevel, floatFactoryPair.getLeft(), floatFactoryPair.getRight());
    }

    public DualModeFloatFactory(final int concurrencyLevel, final FloatFactory floatFactory) {
        this(concurrencyLevel, floatFactory, floatFactory);
    }

    @Override
    public float create() {
        if (concurrencyLevelState.getCurrent() > 0) {
            return concurrentFloatFactory.create();
        }

        return defaultFloatFactory.create();
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
    }
}
