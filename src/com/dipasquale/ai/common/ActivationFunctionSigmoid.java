package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivationFunctionSigmoid implements ActivationFunction {
    @Serial
    private static final long serialVersionUID = 530244118549052601L;
    private static final ActivationFunctionSigmoid INSTANCE = new ActivationFunctionSigmoid();

    public static ActivationFunctionSigmoid getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) { // (-89f, 17f)
        return 1f / (1f + (float) Math.exp(-input));
    }

    @Override
    public String toString() {
        return "Sigmoid";
    }
}
