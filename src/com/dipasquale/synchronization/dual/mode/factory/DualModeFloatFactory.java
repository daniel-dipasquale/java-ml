package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeFloatFactory implements FloatFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6491310596371883595L;
    private final FloatFactory concurrentFloatFactory;
    private final FloatFactory defaultFloatFactory;
    private FloatFactory selectedFloatFactory;

    public DualModeFloatFactory(final int concurrencyLevel, final FloatFactory concurrentFloatFactory, final FloatFactory defaultFloatFactory) {
        this.concurrentFloatFactory = concurrentFloatFactory;
        this.defaultFloatFactory = defaultFloatFactory;
        this.selectedFloatFactory = select(concurrencyLevel, concurrentFloatFactory, defaultFloatFactory);
    }

    public DualModeFloatFactory(final int concurrencyLevel, final FloatFactory floatFactory) {
        this(concurrencyLevel, floatFactory, floatFactory);
    }

    private static FloatFactory select(final int concurrencyLevel, final FloatFactory concurrentFloatFactory, final FloatFactory defaultFloatFactory) {
        if (concurrencyLevel > 0) {
            return concurrentFloatFactory;
        }

        return defaultFloatFactory;
    }

    @Override
    public float create() {
        return selectedFloatFactory.create();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        selectedFloatFactory = select(concurrencyLevel, concurrentFloatFactory, defaultFloatFactory);
    }
}
