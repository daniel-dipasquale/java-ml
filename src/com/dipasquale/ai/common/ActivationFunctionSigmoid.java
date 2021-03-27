package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ActivationFunctionSigmoid implements ActivationFunction {
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
