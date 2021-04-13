package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ActivationFunctionFactoryLiteral implements ActivationFunctionFactory {
    @Serial
    private static final long serialVersionUID = -6106695402162393174L;
    private final ActivationFunction activationFunction;

    @Override
    public ActivationFunction create() {
        return activationFunction;
    }

    @Override
    public ActivationFunctionFactory selectContended(final boolean contended) {
        return this;
    }
}
