package com.dipasquale.ai.rl.neat.function.activation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StepActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 6097503817128600011L;
    private static final StepActivationFunction INSTANCE = new StepActivationFunction();

    public static StepActivationFunction getInstance() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return input > 0f ? 1f : 0f;
    }

    @Override
    public String toString() {
        return "Step";
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
