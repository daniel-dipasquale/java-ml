package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface IntegerFactory extends Serializable {
    int create();

    static IntegerFactory createLiteral(final int value) {
        return new IntegerFactoryLiteral(value);
    }

    static IntegerFactory createIllegalState(final String message) {
        return new IntegerFactoryIllegalStateException(message);
    }

    static IntegerFactory createCyclic(final List<? extends IntegerFactory> factories, final boolean contended) {
        if (!contended) {
            return new IntegerFactoryCyclic(factories);
        }

        return new IntegerFactoryCyclicCas(factories);
    }

    static IntegerFactory createRandom(final RandomSupportFloat randomSupport, final int min, final int max) {
        return new IntegerFactoryRandom(randomSupport, min, max);
    }
}
