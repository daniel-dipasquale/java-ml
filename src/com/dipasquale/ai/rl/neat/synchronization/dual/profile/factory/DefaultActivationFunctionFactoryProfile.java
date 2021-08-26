package com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class DefaultActivationFunctionFactoryProfile extends AbstractObjectProfile<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;

    public DefaultActivationFunctionFactoryProfile(final boolean concurrent, final Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair) {
        super(concurrent, new DefaultActivationFunctionFactory(true, activationFunctionTypeFactoryPair.getLeft()), new DefaultActivationFunctionFactory(false, activationFunctionTypeFactoryPair.getRight()));
    }

    private static ActivationFunction create(final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory, final boolean concurrent) {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, concurrent);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 1968676555308264940L;
        private final boolean concurrent;
        private final EnumFactory<ActivationFunctionType> activationFunctionTypeFactory;

        @Override
        public ActivationFunction create() {
            return DefaultActivationFunctionFactoryProfile.create(activationFunctionTypeFactory, concurrent);
        }
    }
}
