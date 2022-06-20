package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SumFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 2866239540588502526L;
    private static final SumFitnessControllerFactory INSTANCE = new SumFitnessControllerFactory();

    public static SumFitnessControllerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public FitnessController create() {
        return new SumFitnessController();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
