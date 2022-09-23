package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.common.factory.EnumFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ActivationFunctionTypeFactory implements ActivationFunctionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;
    private final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory;

    @Override
    public ActivationFunction create() {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return activationFunctionType.getReference();
    }
}
