package com.dipasquale.ai.common.function.activation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SigmoidActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 530244118549052601L;
    private static final SigmoidActivationFunction INSTANCE = new SigmoidActivationFunction();

    public static SigmoidActivationFunction getInstance() {
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