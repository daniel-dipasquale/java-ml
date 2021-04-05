package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class ActivationFunctionSigmoid implements ActivationFunction {
    @Serial
    private static final long serialVersionUID = 530244118549052601L;

    @Override
    public float forward(final float input) {
        float inputFixed = Float.compare(input, 0f) >= 0
                ? Math.min(input, 100f)
                : Math.max(input, -100f);

        return 1f / (1f + (float) Math.exp(-inputFixed));
    }

    @Override
    public String toString() {
        return "Sigmoid";
    }
}
