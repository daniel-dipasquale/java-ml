package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 7594384895125059543L;
    private final ActivationFunctionFactory activationFunctionFactory;

    @Override
    public ActivationFunction create() {
        return activationFunctionFactory.create();
    }
}
