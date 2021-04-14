package com.dipasquale.concurrent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerBiFactoryIllegalState implements IntegerBiFactory {
    @Serial
    private static final long serialVersionUID = -4508324921538830026L;
    private final String message;

    @Override
    public int create() {
        throw new IllegalStateException(message);
    }

    @Override
    public IntegerBiFactory selectContended(final boolean contended) {
        return this;
    }
}
