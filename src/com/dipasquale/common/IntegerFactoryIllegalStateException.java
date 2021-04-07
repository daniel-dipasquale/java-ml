package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryIllegalStateException implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -2585231472095047546L;
    private final String message;

    @Override
    public int create() {
        throw new IllegalStateException(message);
    }
}
