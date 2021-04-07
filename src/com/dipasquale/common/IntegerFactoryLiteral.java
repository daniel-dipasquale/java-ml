package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryLiteral implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -7513472763937741579L;
    private final int value;

    @Override
    public int create() {
        return value;
    }
}
