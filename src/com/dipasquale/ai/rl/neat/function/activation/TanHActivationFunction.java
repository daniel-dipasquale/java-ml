package com.dipasquale.ai.rl.neat.function.activation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TanHActivationFunction implements ActivationFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 8400985575009442218L;
    private static final TanHActivationFunction INSTANCE = new TanHActivationFunction();

    public static TanHActivationFunction getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return (float) Math.tanh(input);
    }

    @Override
    public String toString() {
        return "TanH";
    }
}
