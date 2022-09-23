package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.common.factory.EnumFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class OutputActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -6687884858709091960L;
    private final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory;
    private final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory;

    private static ActivationFunctionType createActivationFunctionType(final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        OutputActivationFunctionType outputActivationFunctionType = outputActivationFunctionTypeFactory.create();

        return switch (outputActivationFunctionType) {
            case COPY_FROM_HIDDEN -> hiddenActivationFunctionTypeFactory.create();

            default -> outputActivationFunctionType.getTranslated();
        };
    }

    @Override
    public ActivationFunction create() {
        ActivationFunctionType activationFunctionType = createActivationFunctionType(outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);

        return activationFunctionType.getReference();
    }
}
