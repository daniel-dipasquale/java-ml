package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LastValueFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 422605264854317067L;
    private static final LastValueFitnessControllerFactory INSTANCE = new LastValueFitnessControllerFactory();

    public static LastValueFitnessControllerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessController create() {
        return new LastValueFitnessController();
    }
}
