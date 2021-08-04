package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public final class DefaultActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;
    private final Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair;
    @Getter(AccessLevel.PROTECTED)
    private final ActivationFunctionFactory on;
    @Getter(AccessLevel.PROTECTED)
    private final ActivationFunctionFactory off;

    public DefaultActivationFunctionFactorySwitcher(final boolean isOn, final Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair) {
        super(isOn);
        this.activationFunctionTypeFactoryPair = activationFunctionTypeFactoryPair;
        this.on = new OnActivationFunctionFactory();
        this.off = new OffActivationFunctionFactory();
    }

    private ActivationFunction create(final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory, final boolean isOn) {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn);
    }

    private final class OnActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 1968676555308264940L;

        @Override
        public ActivationFunction create() {
            return DefaultActivationFunctionFactorySwitcher.this.create(activationFunctionTypeFactoryPair.getLeft(), true);
        }
    }

    private final class OffActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -4086077924002060705L;

        @Override
        public ActivationFunction create() {
            return DefaultActivationFunctionFactorySwitcher.this.create(activationFunctionTypeFactoryPair.getRight(), false);
        }
    }
}
