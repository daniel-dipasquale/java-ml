package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.concurrent.EnumBiFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsActivationFunctionFactoryDefault implements ActivationFunctionFactory {
    @Serial
    private static final long serialVersionUID = 2087952631887422948L;
    private final EnumBiFactory<SettingsActivationFunction> activationFunctionFactory;
    private final SettingsActivationFunctionFactoryContended contendedFactory = new SettingsActivationFunctionFactoryContended();

    private ActivationFunction create(final boolean contended) {
        SettingsActivationFunction activationFunction = activationFunctionFactory.create();

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

    private final class SettingsActivationFunctionFactoryContended implements ActivationFunctionFactory {
        @Serial
        private static final long serialVersionUID = -1062004666978860258L;

        @Override
        public ActivationFunction create() {
            return SettingsActivationFunctionFactoryDefault.this.create(true);
        }

        @Override
        public ActivationFunctionFactory selectContended(final boolean contended) {
            return SettingsActivationFunctionFactoryDefault.this.selectContended(contended);
        }
    }
}
