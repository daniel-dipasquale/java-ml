package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivationFunctionStep implements ActivationFunction {
    @Serial
    private static final long serialVersionUID = 6097503817128600011L;
    private static final ActivationFunctionStep INSTANCE = new ActivationFunctionStep();

    public static ActivationFunctionStep getInstance() {
        return INSTANCE;
    }

    @Override
    public float forward(final float input) {
        return input >= 0f ? 1f : 0f;
    }

    @Override
    public String toString() {
        return "Step";
    }
}
