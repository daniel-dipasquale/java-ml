package com.dipasquale.ai.common;

@FunctionalInterface
public interface ActivationFunctionFactory {
    ActivationFunction next();
}
