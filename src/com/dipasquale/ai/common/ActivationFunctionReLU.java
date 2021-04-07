package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivationFunctionReLU implements ActivationFunction {
    @Serial
    private static final long serialVersionUID = 1087976438012155468L;
    private static final ActivationFunctionReLU INSTANCE = new ActivationFunctionReLU();

    public static ActivationFunctionReLU getInstance() {
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
