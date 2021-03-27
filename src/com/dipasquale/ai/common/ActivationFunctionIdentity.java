package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ActivationFunctionIdentity implements ActivationFunction {
    @Override
    public float forward(final float input) {
        return input;
    }

    @Override
    public String toString() {
        return "Identity";
    }
}
