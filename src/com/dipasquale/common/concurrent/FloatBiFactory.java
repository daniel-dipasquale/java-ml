package com.dipasquale.common.concurrent;

import com.dipasquale.common.FloatFactory;

import java.util.List;

public interface FloatBiFactory extends FloatFactory {
    FloatBiFactory selectContended(boolean contended);

    static FloatBiFactory createLiteral(final float value) {
        return new FloatBiFactoryLiteral(value);
    }

    static FloatBiFactory createIllegalState(final String message) {
        return new FloatBiFactoryIllegalState(message);
    }

    static FloatBiFactory createCyclic(final List<? extends FloatBiFactory> factories) {
        return new FloatBiFactoryCyclic(factories);
    }
}
