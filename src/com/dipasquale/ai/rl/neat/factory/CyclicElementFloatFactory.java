package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.CyclicIntegerValue;
import com.dipasquale.common.factory.FloatFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class CyclicElementFloatFactory implements FloatFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 1086646213705454566L;
    private final List<FloatFactory> floatFactories;
    private final CyclicIntegerValue index;

    public CyclicElementFloatFactory(final List<FloatFactory> floatFactories, final int index) {
        this.floatFactories = List.copyOf(floatFactories);
        this.index = new CyclicIntegerValue(floatFactories.size(), -1, index);
    }

    public CyclicElementFloatFactory(final List<FloatFactory> floatFactories) {
        this(floatFactories, 0);
    }

    @Override
    public float create() {
        FloatFactory floatFactory = floatFactories.get(index.increment());

        return floatFactory.create();
    }
}
