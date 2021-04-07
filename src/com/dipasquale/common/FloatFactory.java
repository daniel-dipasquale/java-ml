package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface FloatFactory extends Serializable { // TODO: add a differentiator between singleton or transient
    float create();

    static FloatFactory createLiteral(final float value) {
        return new FloatFactoryLiteral(value);
    }

    static FloatFactory createIllegalState(final String message) {
        return new FloatFactoryIllegalStateException(message);
    }

    static FloatFactory createCyclic(final List<? extends FloatFactory> factories, final boolean contended) {
        if (!contended) {
            return new FloatFactoryCyclic(factories);
        }

        return new FloatFactoryCyclicCas(factories);
    }

    static FloatFactory createRandom(final RandomSupportFloat randomSupport, final float min, final float max) {
        return new FloatFactoryRandom(randomSupport, min, max);
    }
}
