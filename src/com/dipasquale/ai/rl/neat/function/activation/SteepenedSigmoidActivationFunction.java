package com.dipasquale.ai.rl.neat.function.activation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SteepenedSigmoidActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = -2180574309538173061L;
    private static final SteepenedSigmoidActivationFunction INSTANCE = new SteepenedSigmoidActivationFunction();

    public static SteepenedSigmoidActivationFunction getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return 1f / (1f + (float) Math.exp(-4.9f * input));
    }

    @Override
    public String toString() {
        return "SteepenedSigmoid";
    }
}
