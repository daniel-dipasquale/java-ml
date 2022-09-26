package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.common.factory.FloatFactory;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public final class GenerationalFloatFactory implements GenerationalFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 1774911324505082169L;
    private final FloatFactory floatFactory;
    @Getter
    private float value;

    public GenerationalFloatFactory(final FloatFactory floatFactory) {
        this.floatFactory = floatFactory;
        this.value = floatFactory.create();
    }

    @Override
    public void reinitialize() {
        value = floatFactory.create();
    }
}
