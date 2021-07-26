package com.dipasquale.common.factory;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class IllegalStateFloatFactory implements FloatFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -4494498136112446727L;
    private final String message;

    @Override
    public float create() {
        throw new IllegalStateException(message);
    }
}
