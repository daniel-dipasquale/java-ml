package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.common.factory.IntegerFactory;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public final class GenerationalIntegerFactory implements GenerationalFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8052362758784529107L;
    private final IntegerFactory integerFactory;
    @Getter
    private int value;

    public GenerationalIntegerFactory(final IntegerFactory integerFactory) {
        this.integerFactory = integerFactory;
        this.value = integerFactory.create();
    }

    @Override
    public void reinitialize() {
        value = integerFactory.create();
    }
}
