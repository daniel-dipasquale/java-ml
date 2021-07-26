package com.dipasquale.common.factory;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LiteralIntegerFactory implements IntegerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 6776848635010009469L;
    private final int value;

    @Override
    public int create() {
        return value;
    }
}
