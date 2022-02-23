package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeActivationFunctionFactory<T extends EnumFactory<ActivationFunctionType> & DualModeObject> implements ActivationFunctionFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;
    private final T activationFunctionTypeFactory;

    @Override
    public ActivationFunction create() {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return activationFunctionType.getReference();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        activationFunctionTypeFactory.activateMode(concurrencyLevel);
    }
}
