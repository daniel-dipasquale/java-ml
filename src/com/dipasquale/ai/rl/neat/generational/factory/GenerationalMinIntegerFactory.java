package com.dipasquale.ai.rl.neat.generational.factory;

import com.dipasquale.common.factory.IntegerFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenerationalMinIntegerFactory implements GenerationalFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 4041701231695856473L;
    private final GenerationalIntegerFactory factoryA;
    private final GenerationalIntegerFactory factoryB;
    @Getter
    private int value;

    private static int getMinimumValue(final GenerationalIntegerFactory factoryA, final GenerationalIntegerFactory factoryB) {
        return Math.min(factoryA.getValue(), factoryB.getValue());
    }

    public GenerationalMinIntegerFactory(final GenerationalIntegerFactory factoryA, final GenerationalIntegerFactory factoryB) {
        this(factoryA, factoryB, getMinimumValue(factoryA, factoryB));
    }

    public GenerationalMinIntegerFactory(final IntegerFactory factoryA, final IntegerFactory factoryB) {
        this(new GenerationalIntegerFactory(factoryA), new GenerationalIntegerFactory(factoryB));
    }

    @Override
    public void reinitialize() {
        factoryA.reinitialize();
        factoryB.reinitialize();
        value = getMinimumValue(factoryA, factoryB);
    }
}
