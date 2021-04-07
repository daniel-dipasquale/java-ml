package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryIllegalStateException implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 574998598763910967L;
    private final String message;

    @Override
    public float create() {
        throw new IllegalStateException(message);
    }
}
