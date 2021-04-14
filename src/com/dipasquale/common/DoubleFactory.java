package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface DoubleFactory extends Serializable {
    double create();

    static DoubleFactory createLiteral(final double value) {
        return () -> value;
    }
}
