package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.IdentityActivationFunction;
import com.dipasquale.ai.common.function.activation.ReLUActivationFunction;
import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.StepActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class DualModeActivationFunctionFactory<T extends EnumFactory<ActivationFunctionType> & DualModeObject> implements ActivationFunctionFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6589751898445384359L;

    private static final Map<ActivationFunctionType, ActivationFunction> ACTIVATION_FUNCTIONS_MAP = ImmutableMap.<ActivationFunctionType, ActivationFunction>builder()
            .put(ActivationFunctionType.IDENTITY, IdentityActivationFunction.getInstance())
            .put(ActivationFunctionType.RE_LU, ReLUActivationFunction.getInstance())
            .put(ActivationFunctionType.SIGMOID, SigmoidActivationFunction.getInstance())
            .put(ActivationFunctionType.TAN_H, TanHActivationFunction.getInstance())
            .put(ActivationFunctionType.STEP, StepActivationFunction.getInstance())
            .build();

    private static final List<ActivationFunction> ACTIVATION_FUNCTIONS = ImmutableList.copyOf(ACTIVATION_FUNCTIONS_MAP.values());
    private final T activationFunctionTypeFactory;
    private final DualModeRandomSupport randomSupport;

    public DualModeActivationFunctionFactory(final DualModeRandomSupport randomSupport, final T activationFunctionTypeFactory) {
        this.randomSupport = randomSupport;
        this.activationFunctionTypeFactory = DualModeObject.activateMode(activationFunctionTypeFactory, randomSupport.concurrencyLevel());
    }

    static ActivationFunction getActivationFunction(final ActivationFunctionType activationFunctionType, final RandomSupport randomSupport) {
        return switch (activationFunctionType) {
            case RANDOM -> {
                int index = randomSupport.next(0, ACTIVATION_FUNCTIONS.size());

                yield ACTIVATION_FUNCTIONS.get(index);
            }

            default -> ACTIVATION_FUNCTIONS_MAP.get(activationFunctionType);
        };
    }

    @Override
    public ActivationFunction create() {
        ActivationFunctionType activationFunctionType = activationFunctionTypeFactory.create();

        return getActivationFunction(activationFunctionType, randomSupport);
    }

    @Override
    public int concurrencyLevel() {
        return activationFunctionTypeFactory.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        activationFunctionTypeFactory.activateMode(concurrencyLevel);
        randomSupport.activateMode(concurrencyLevel);
    }
}
