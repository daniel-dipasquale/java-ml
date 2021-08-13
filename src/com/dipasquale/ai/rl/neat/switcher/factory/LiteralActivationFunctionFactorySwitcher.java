/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class LiteralActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = 7594384895125059543L;
    private final ActivationFunctionFactory activationFunctionFactory;

    public LiteralActivationFunctionFactorySwitcher(final boolean isOn, final ActivationFunctionType activationFunctionType) {
        super(isOn, null, null);
        this.activationFunctionFactory = new DefaultActivationFunctionFactory(Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn));
    }

    @Override
    protected ActivationFunctionFactory getOnObject() {
        return activationFunctionFactory;
    }

    @Override
    protected ActivationFunctionFactory getOffObject() {
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
