package com.dipasquale.common.factory;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LiteralObjectFactory<T> implements ObjectFactory<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -1409264609555076644L;
    private final T value;

    @Override
    public T create() {
        return value;
    }
}
