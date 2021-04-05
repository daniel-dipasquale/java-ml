package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface FloatFactory extends Serializable {
    float create();

    static FloatFactory createLiteral(final float value) {
        return () -> value;
    }
}
