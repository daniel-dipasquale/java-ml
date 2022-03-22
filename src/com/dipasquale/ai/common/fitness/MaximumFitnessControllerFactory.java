package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaximumFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8213265993365433024L;
    private static final MaximumFitnessControllerFactory INSTANCE = new MaximumFitnessControllerFactory();

    public static MaximumFitnessControllerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessController create() {
        return new MaximumFitnessController();
    }
}
