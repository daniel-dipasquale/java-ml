package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
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
            RandomSupportFloat randomSupport = sp.getRandomSupport(type, false);
            RandomSupportFloat randomSupportContended = sp.getRandomSupport(type, true);
            FloatFactory factory = FloatFactory.createRandom(randomSupport, min, max, randomSupportContended);

            return factory.selectContended(sp.isEnabled());
        };

        return new SettingsFloatNumber(factoryCreator);
    }

    FloatFactory createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface FloatFactoryCreator {
        FloatFactory create(SettingsParallelism parallelism);
    }
}
