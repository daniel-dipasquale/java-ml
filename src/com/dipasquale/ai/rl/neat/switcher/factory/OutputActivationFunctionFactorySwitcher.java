package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class OutputActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = -6687884858709091960L;

    public OutputActivationFunctionFactorySwitcher(final boolean isOn, final Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair, final Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair) {
        super(isOn, new DefaultActivationFunctionFactory(true, outputActivationFunctionTypeFactoryPair.getLeft(), hiddenActivationFunctionTypeFactoryPair.getLeft()), new DefaultActivationFunctionFactory(false, outputActivationFunctionTypeFactoryPair.getRight(), hiddenActivationFunctionTypeFactoryPair.getRight()));
    }

    private static ActivationFunctionType createActivationFunctionType(final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        OutputActivationFunctionType outputActivationFunctionType = outputActivationFunctionTypeFactory.create();

        return switch (outputActivationFunctionType) {
            case COPY_FROM_HIDDEN -> hiddenActivationFunctionTypeFactory.create();

            default -> outputActivationFunctionType.getTranslated();
        };
    }

    private static ActivationFunction create(final boolean isOn, final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        ActivationFunctionType activationFunctionType = createActivationFunctionType(outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);

        return Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3984638047934118973L;
        private final boolean isOn;
        private final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory;
        private final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory;

        @Override
        public ActivationFunction create() {
            return OutputActivationFunctionFactorySwitcher.create(isOn, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);
        }
    }
}
