package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.concurrent.FloatBiFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsFloatNumber {
    private final FloatFactoryCreator factoryCreator;

    public static SettingsFloatNumber literal(final float number) {
        FloatFactoryCreator factoryCreator = sp -> FloatBiFactory.createLiteral(number);

        return new SettingsFloatNumber(factoryCreator);
    }

    public static SettingsFloatNumber random(final SettingsRandomType type, final float min, final float max) {
        FloatFactoryCreator factoryCreator = sp -> {
            FloatBiFactory factory = new SettingsFloatFactoryRandom(type, min, max);

            return factory.selectContended(sp.isEnabled());
        };

        return new SettingsFloatNumber(factoryCreator);
    }

    FloatBiFactory createFactory(final SettingsParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface FloatFactoryCreator {
        FloatBiFactory create(SettingsParallelismSupport parallelism);
    }
}
