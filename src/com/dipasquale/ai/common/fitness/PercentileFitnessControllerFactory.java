package com.dipasquale.ai.common.fitness;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class PercentileFitnessControllerFactory implements FitnessControllerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2840305259809237496L;
    private final float percentage;

    @Override
    public FitnessController create() {
        return new PercentileFitnessController(percentage);
    }
}
