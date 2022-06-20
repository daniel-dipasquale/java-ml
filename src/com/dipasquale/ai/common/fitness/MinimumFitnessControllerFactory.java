package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinimumFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -58721796874663202L;
    private static final MinimumFitnessControllerFactory INSTANCE = new MinimumFitnessControllerFactory();

    public static MinimumFitnessControllerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public FitnessController create() {
        return new MinimumFitnessController();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
