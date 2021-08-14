package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class DefaultActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;

    public DefaultActivationFunctionFactorySwitcher(final boolean isOn, final Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair) {
        super(isOn, new DefaultActivationFunctionFactory(true, activationFunctionTypeFactoryPair.getLeft()), new DefaultActivationFunctionFactory(false, activationFunctionTypeFactoryPair.getRight()));
    }

    private static ActivationFunction create(final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory, final boolean isOn) {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 1968676555308264940L;
        private final boolean isOn;
        private final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory;

        @Override
        public ActivationFunction create() {
            return DefaultActivationFunctionFactorySwitcher.create(activationFunctionTypeFactory, isOn);
        }
    }
}
