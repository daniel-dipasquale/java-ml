package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeStrategyActivationFunctionFactory<T extends ActivationFunctionFactory & DualModeObject> implements ActivationFunctionFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 7594384895125059543L;
    private final T activationFunctionFactory;

    @Override
    public ActivationFunction create() {
        return activationFunctionFactory.create();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        activationFunctionFactory.activateMode(concurrencyLevel);
    }
}
