package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AverageFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3798311517368633267L;
    private static final AverageFitnessControllerFactory INSTANCE = new AverageFitnessControllerFactory();

    public static AverageFitnessControllerFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public FitnessController create() {
        return new AverageFitnessController();
    }
}
