package com.dipasquale.ai.rl.neat.switcher;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class OutputActivationFunctionFactorySwitcher extends AbstractObjectSwitcher<ActivationFunctionFactory> {
    @Serial
    private static final long serialVersionUID = -6687884858709091960L;
    private final Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair;
    private final Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair;
    @Getter(AccessLevel.PROTECTED)
    private final ActivationFunctionFactory on;
    @Getter(AccessLevel.PROTECTED)
    private final ActivationFunctionFactory off;

    public OutputActivationFunctionFactorySwitcher(final boolean isOn, final Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair, final Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair) {
        super(isOn);
        this.outputActivationFunctionTypeFactoryPair = outputActivationFunctionTypeFactoryPair;
        this.hiddenActivationFunctionTypeFactoryPair = hiddenActivationFunctionTypeFactoryPair;
        this.on = new OnActivationFunctionFactory();
        this.off = new OffActivationFunctionFactory();
    }

    private static ActivationFunctionType createActivationFunctionType(final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        OutputActivationFunctionType outputActivationFunctionType = outputActivationFunctionTypeFactory.create();

        return switch (outputActivationFunctionType) {
            case COPY_FROM_HIDDEN -> hiddenActivationFunctionTypeFactory.create();

            default -> outputActivationFunctionType.getTranslated();
        };
    }

    private ActivationFunction create(final boolean isOn, final EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        ActivationFunctionType activationFunctionType = createActivationFunctionType(outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);

        return Constants.getActivationFunction(activationFunctionType, RandomType.UNIFORM, isOn);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OnActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3984638047934118973L;

        @Override
        public ActivationFunction create() {
            EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory = outputActivationFunctionTypeFactoryPair.getLeft();
            EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory = hiddenActivationFunctionTypeFactoryPair.getLeft();

            return OutputActivationFunctionFactorySwitcher.this.create(true, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OffActivationFunctionFactory implements ActivationFunctionFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 9011523118745664669L;

        @Override
        public ActivationFunction create() {
            EnumFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory = outputActivationFunctionTypeFactoryPair.getRight();
            EnumFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory = hiddenActivationFunctionTypeFactoryPair.getRight();

            return OutputActivationFunctionFactorySwitcher.this.create(false, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);
        }
    }
}
