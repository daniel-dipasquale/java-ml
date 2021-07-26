package com.dipasquale.ai.rl.neat.switcher;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;

import java.io.Serial;
import java.io.Serializable;

public final class LiteralActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = 7594384895125059543L;
    private final ActivationFunction activationFunction;
    private final ActivationFunctionFactory activationFunctionFactory;

    public LiteralActivationFunctionFactorySwitcher(final boolean isOn, final ActivationFunctionType activationFunctionType) {
        super(isOn);
        this.activationFunction = Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn);
        this.activationFunctionFactory = new DefaultActivationFunctionFactory();
    }

    @Override
    protected ActivationFunctionFactory getOn() {
        return activationFunctionFactory;
    }

    @Override
    protected ActivationFunctionFactory getOff() {
        return activationFunctionFactory;
    }

    private final class DefaultActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 1687829281254664934L;

        @Override
        public ActivationFunction create() {
            return activationFunction;
        }
    }
}
