package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeOutputActivationFunctionFactory<TOutputActivationFunctionTypeFactory extends EnumFactory<OutputActivationFunctionType> & DualModeObject, THiddenActivationFunctionTypeFactory extends EnumFactory<ActivationFunctionType> & DualModeObject> implements ActivationFunctionFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6687884858709091960L;
    private final DualModeRandomSupport randomSupport;
    private final TOutputActivationFunctionTypeFactory outputActivationFunctionTypeFactory;
    private final THiddenActivationFunctionTypeFactory hiddenActivationFunctionTypeFactory;

    public DualModeOutputActivationFunctionFactory(final DualModeRandomSupport randomSupport, final TOutputActivationFunctionTypeFactory outputActivationFunctionTypeFactory, final THiddenActivationFunctionTypeFactory hiddenActivationFunctionTypeFactory) {
        this.randomSupport = randomSupport;
        this.outputActivationFunctionTypeFactory = DualModeObject.activateMode(outputActivationFunctionTypeFactory, randomSupport.concurrencyLevel());
        this.hiddenActivationFunctionTypeFactory = DualModeObject.activateMode(hiddenActivationFunctionTypeFactory, randomSupport.concurrencyLevel());
    }

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

        return DualModeActivationFunctionFactory.getActivationFunction(activationFunctionType, randomSupport);
    }

    @Override
    public int concurrencyLevel() {
        return randomSupport.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        randomSupport.activateMode(concurrencyLevel);
        outputActivationFunctionTypeFactory.activateMode(concurrencyLevel);
        hiddenActivationFunctionTypeFactory.activateMode(concurrencyLevel);
    }
}
