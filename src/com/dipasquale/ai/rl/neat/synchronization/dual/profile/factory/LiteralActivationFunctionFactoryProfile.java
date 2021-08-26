package com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class LiteralActivationFunctionFactoryProfile extends AbstractObjectProfile<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = 7594384895125059543L;
    private final ActivationFunctionFactory activationFunctionFactory;

    public LiteralActivationFunctionFactoryProfile(final boolean concurrent, final ActivationFunctionType activationFunctionType) {
        super(concurrent, null, null);
        this.activationFunctionFactory = new DefaultActivationFunctionFactory(Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, concurrent));
    }

    @Override
    protected ActivationFunctionFactory getConcurrentObject() {
        return activationFunctionFactory;
    }

    @Override
    protected ActivationFunctionFactory getDefaultObject() {
        return activationFunctionFactory;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 1687829281254664934L;
        private final ActivationFunction activationFunction;

        @Override
        public ActivationFunction create() {
            return activationFunction;
        }
    }
}
