package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeIntegerFactory implements IntegerFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 3095713724252391672L;
    private final IntegerFactory concurrentIntegerFactory;
    private final IntegerFactory defaultIntegerFactory;
    private IntegerFactory selectedIntegerFactory;

    public DualModeIntegerFactory(final int concurrencyLevel, final IntegerFactory concurrentIntegerFactory, final IntegerFactory defaultIntegerFactory) {
        this.concurrentIntegerFactory = concurrentIntegerFactory;
        this.defaultIntegerFactory = defaultIntegerFactory;
        this.selectedIntegerFactory = select(concurrencyLevel, concurrentIntegerFactory, defaultIntegerFactory);
    }

    public DualModeIntegerFactory(final int concurrencyLevel, final IntegerFactory integerFactory) {
        this(concurrencyLevel, integerFactory, integerFactory);
    }

    private static IntegerFactory select(final int concurrencyLevel, final IntegerFactory concurrentIntegerFactory, final IntegerFactory defaultIntegerFactory) {
        if (concurrencyLevel > 0) {
            return concurrentIntegerFactory;
        }

        return defaultIntegerFactory;
    }

    @Override
    public int create() {
        return selectedIntegerFactory.create();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        selectedIntegerFactory = select(concurrencyLevel, concurrentIntegerFactory, defaultIntegerFactory);
    }
}
