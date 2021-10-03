package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeIntegerFactory implements IntegerFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 3095713724252391672L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final IntegerFactory concurrentIntegerFactory;
    private final IntegerFactory defaultIntegerFactory;

    public DualModeIntegerFactory(final int concurrencyLevel, final IntegerFactory concurrentIntegerFactory, final IntegerFactory defaultIntegerFactory) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentIntegerFactory = concurrentIntegerFactory;
        this.defaultIntegerFactory = defaultIntegerFactory;
    }

    public DualModeIntegerFactory(final int concurrencyLevel, final Pair<IntegerFactory> integerFactoryPair) {
        this(concurrencyLevel, integerFactoryPair.getLeft(), integerFactoryPair.getRight());
    }

    public DualModeIntegerFactory(final int concurrencyLevel, final IntegerFactory integerFactory) {
        this(concurrencyLevel, integerFactory, integerFactory);
    }

    @Override
    public int create() {
        if (concurrencyLevelState.getCurrent() > 0) {
            return concurrentIntegerFactory.create();
        }

        return defaultIntegerFactory.create();
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
