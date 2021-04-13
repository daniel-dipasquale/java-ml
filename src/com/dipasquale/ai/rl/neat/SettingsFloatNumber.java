package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsFloatNumber {
    private final FloatFactoryCreator factoryCreator;

    public static SettingsFloatNumber literal(final float number) {
        FloatFactoryCreator factoryCreator = sp -> FloatFactory.createLiteral(number);

        return new SettingsFloatNumber(factoryCreator);
    }

    public static SettingsFloatNumber random(final SettingsRandomType type, final float min, final float max) {
        FloatFactoryCreator factoryCreator = sp -> {
            FloatFactory factory = new SettingsFloatFactoryRandom(type, min, max);

            return factory.selectContended(sp.isEnabled());
        };

        return new SettingsFloatNumber(factoryCreator);
    }

    FloatFactory createFactory(final SettingsParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface FloatFactoryCreator {
        FloatFactory create(SettingsParallelismSupport parallelism);
    }
}
