package com.dipasquale.ai.common;

import com.dipasquale.common.ObjectFactory;

public interface ActivationFunctionFactory extends ObjectFactory<ActivationFunction> {
    ActivationFunctionFactory selectContended(boolean contended);

    static ActivationFunctionFactory createLiteral(final ActivationFunction value) {
        return new ActivationFunctionFactoryLiteral(value);
    }
}
