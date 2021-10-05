package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeCyclicIntegerCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.google.common.collect.ImmutableList;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class DualModeCyclicFloatFactory<T extends FloatFactory & DualModeObject> implements FloatFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 610333221078013332L;
    private final List<T> floatFactories;
    private final DualModeCyclicIntegerCounter index;

    public DualModeCyclicFloatFactory(final int concurrencyLevel, final List<T> floatFactories, final int index) {
        this.floatFactories = ImmutableList.copyOf(floatFactories);
        this.index = new DualModeCyclicIntegerCounter(concurrencyLevel, floatFactories.size(), -1, index);
    }

    public DualModeCyclicFloatFactory(final int concurrencyLevel, final List<T> floatFactories) {
        this(concurrencyLevel, floatFactories, 0);
    }

    @Override
    public float create() {
        T floatFactory = floatFactories.get(index.increment());

        return floatFactory.create();
    }

    @Override
    public int concurrencyLevel() {
        return index.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        DualModeObject.forEachActivateMode(floatFactories, concurrencyLevel);
        index.activateMode(concurrencyLevel);
    }
}