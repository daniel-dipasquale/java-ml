package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

public interface FloatFactory extends Serializable {
    float create();

    FloatFactory selectContended(boolean contended);

    static FloatFactory createLiteral(final float value) {
        return new FloatFactoryLiteral(value);
    }

    static FloatFactory createIllegalState(final String message) {
        return new FloatFactoryIllegalState(message);
    }

    static FloatFactory createCyclic(final List<? extends FloatFactory> factories) {
        return new FloatFactoryCyclic(factories);
    }
}
