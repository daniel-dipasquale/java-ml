package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class ActivationFunctionTanH implements ActivationFunction {
    @Serial
    private static final long serialVersionUID = 8400985575009442218L;

    @Override
    public float forward(final float input) {
        return (float) Math.tanh(input);
    }

    @Override
    public String toString() {
        return "TanH";
    }
}
