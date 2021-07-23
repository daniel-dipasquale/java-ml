package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerBiFactoryLiteral implements IntegerBiFactory {
    @Serial
    private static final long serialVersionUID = -8019471724010338823L;
    private final int value;

    @Override
    public int create() {
        return value;
    }

    @Override
    public IntegerBiFactory selectContended(final boolean contended) {
        return this;
    }
}
