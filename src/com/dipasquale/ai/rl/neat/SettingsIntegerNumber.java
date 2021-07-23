package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.concurrent.IntegerBiFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsIntegerNumber {
    private final IntegerFactoryCreator factoryCreator;

    public static SettingsIntegerNumber literal(final int number) {
        IntegerFactoryCreator factoryCreator = sp -> IntegerBiFactory.createLiteral(number);

        return new SettingsIntegerNumber(factoryCreator);
    }

    public static SettingsIntegerNumber random(final SettingsRandomType type, final int min, final int max) {
        IntegerFactoryCreator factoryCreator = sp -> {
            IntegerBiFactory factory = new SettingsIntegerFactoryRandom(type, min, max);

            return factory.selectContended(sp.isEnabled());
        };

        return new SettingsIntegerNumber(factoryCreator);
    }

    IntegerBiFactory createFactory(final SettingsParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface IntegerFactoryCreator {
        IntegerBiFactory create(SettingsParallelismSupport parallelism);
    }
}
