package com.dipasquale.ai.common.function.activation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivationFunctionIdentity implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 398303803070080944L;
    private static final ActivationFunctionIdentity INSTANCE = new ActivationFunctionIdentity();

    public static ActivationFunctionIdentity getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return input;
    }

    @Override
    public String toString() {
        return "Identity";
    }
}
