package com.dipasquale.ai.rl.neat.function.activation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReLUActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 1087976438012155468L;
    private static final ReLUActivationFunction INSTANCE = new ReLUActivationFunction();

    public static ReLUActivationFunction getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return Math.max(0f, input);
    }

    @Override
    public String toString() {
        return "ReLU";
    }
}
