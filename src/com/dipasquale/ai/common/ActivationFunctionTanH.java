package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ActivationFunctionTanH implements ActivationFunction {
    @Override
    public float forward(final float input) {
        return (float) Math.tanh(input);
    }

    @Override
    public String toString() {
        return "TanH";
    }
}
