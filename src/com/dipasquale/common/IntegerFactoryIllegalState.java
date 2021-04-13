package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryIllegalState implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -4508324921538830026L;
    private final String message;

    @Override
    public int create() {
        throw new IllegalStateException(message);
    }

    @Override
    public IntegerFactory selectContended(final boolean contended) {
        return this;
    }
}
