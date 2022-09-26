package com.dipasquale.common.factory;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ConstantFloatFactory implements FloatFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7859069922802574669L;
    private final float value;

    @Override
    public float create() {
        return value;
    }
}
