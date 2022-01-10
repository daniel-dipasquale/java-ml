package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.ReLUActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.SteepenedSigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.StepActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public final class DualModeActivationFunctionFactory<T extends EnumFactory<ActivationFunctionType> & DualModeObject> implements ActivationFunctionFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;

    private static final Map<ActivationFunctionType, ActivationFunction> ACTIVATION_FUNCTIONS = Map.ofEntries(
            Map.entry(ActivationFunctionType.IDENTITY, IdentityActivationFunction.getInstance()),
            Map.entry(ActivationFunctionType.RE_LU, ReLUActivationFunction.getInstance()),
            Map.entry(ActivationFunctionType.SIGMOID, SigmoidActivationFunction.getInstance()),
            Map.entry(ActivationFunctionType.STEEPENED_SIGMOID, SteepenedSigmoidActivationFunction.getInstance()),
            Map.entry(ActivationFunctionType.TAN_H, TanHActivationFunction.getInstance()),
            Map.entry(ActivationFunctionType.STEP, StepActivationFunction.getInstance())
    );

    private final T activationFunctionTypeFactory;

    public DualModeActivationFunctionFactory(final int concurrencyLevel, final T activationFunctionTypeFactory) {
        this.activationFunctionTypeFactory = DualModeObject.activateMode(activationFunctionTypeFactory, concurrencyLevel);
    }

    static ActivationFunction getActivationFunction(final ActivationFunctionType activationFunctionType) {
        return ACTIVATION_FUNCTIONS.get(activationFunctionType);
    }

    @Override
    public ActivationFunction create() {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return getActivationFunction(activationFunctionType);
    }

    @Override
    public int concurrencyLevel() {
        return activationFunctionTypeFactory.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        activationFunctionTypeFactory.activateMode(concurrencyLevel);
    }
}
