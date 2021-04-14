package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.concurrent.EnumBiFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsActivationFunctionFactoryOutput implements ActivationFunctionFactory {
    @Serial
    private static final long serialVersionUID = -3202998167478993985L;
    private final EnumBiFactory<SettingsOutputActivationFunction> outputActivationFunctionFactory;
    private final EnumBiFactory<SettingsActivationFunction> hiddenActivationFunctionFactory;
    private final SettingsActivationFunctionFactoryOutputContended contendedFactory = new SettingsActivationFunctionFactoryOutputContended();

    private SettingsActivationFunction createOutputActivationFunction() {
        SettingsOutputActivationFunction outputActivationFunction = outputActivationFunctionFactory.create();

        return switch (outputActivationFunction) {
            case COPY_FROM_HIDDEN -> hiddenActivationFunctionFactory.create();

            default -> outputActivationFunction.getTranslated();
        };
    }

    private ActivationFunction create(final boolean contended) {
        SettingsActivationFunction activationFunction = createOutputActivationFunction();

        return SettingsConstants.getActivationFunction(activationFunction, contended);
    }

    @Override
    public ActivationFunction create() {
        return create(false);
    }

    @Override
    public ActivationFunctionFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedFactory;
    }

    private final class SettingsActivationFunctionFactoryOutputContended implements ActivationFunctionFactory {
        @Serial
        private static final long serialVersionUID = -5529862004456289463L;

        @Override
        public ActivationFunction create() {
            return SettingsActivationFunctionFactoryOutput.this.create(true);
        }

        @Override
        public ActivationFunctionFactory selectContended(final boolean contended) {
            return SettingsActivationFunctionFactoryOutput.this.selectContended(contended);
        }
    }
}
