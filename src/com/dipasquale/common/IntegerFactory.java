package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

public interface IntegerFactory extends Serializable {
    int create();

    IntegerFactory selectContended(boolean contended);

    static IntegerFactory createLiteral(final int value) {
        return new IntegerFactoryLiteral(value);
    }

    static IntegerFactory createIllegalState(final String message) {
        return new IntegerFactoryIllegalState(message);
    }

    static IntegerFactory createCyclic(final List<? extends IntegerFactory> factories) {
        return new IntegerFactoryCyclic(factories);
    }
}
