package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.common.EnumFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsActivationFunctionFactoryDefault implements ActivationFunctionFactory {
    @Serial
    private static final long serialVersionUID = 2087952631887422948L;
    private final EnumFactory<SettingsActivationFunction> activationFunctionFactory;
    private final SettingsActivationFunctionFactoryDefaultContended contendedFactory = new SettingsActivationFunctionFactoryDefaultContended();

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

    private final class SettingsActivationFunctionFactoryDefaultContended implements ActivationFunctionFactory {
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
