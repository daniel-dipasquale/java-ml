package com.dipasquale.concurrent;

import com.dipasquale.common.IntegerFactory;

import java.util.List;

public interface IntegerBiFactory extends IntegerFactory {
    IntegerBiFactory selectContended(boolean contended);

    static IntegerBiFactory createLiteral(final int value) {
        return new IntegerBiFactoryLiteral(value);
    }

    static IntegerBiFactory createIllegalState(final String message) {
        return new IntegerBiFactoryIllegalState(message);
    }

    static IntegerBiFactory createCyclic(final List<? extends IntegerBiFactory> factories) {
        return new IntegerBiFactoryCyclic(factories);
    }
}
