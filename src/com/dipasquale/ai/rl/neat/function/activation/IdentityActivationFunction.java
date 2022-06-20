package com.dipasquale.ai.rl.neat.function.activation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdentityActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 398303803070080944L;
    private static final IdentityActivationFunction INSTANCE = new IdentityActivationFunction();

    public static IdentityActivationFunction getInstance() {
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

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
