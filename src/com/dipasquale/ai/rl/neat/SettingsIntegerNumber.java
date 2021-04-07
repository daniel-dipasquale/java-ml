package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IntegerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsIntegerNumber {
    private final IntegerFactoryCreator factoryCreator;

    public static SettingsIntegerNumber literal(final int number) {
        IntegerFactoryCreator factoryCreator = sp -> IntegerFactory.createLiteral(number);

        return new SettingsIntegerNumber(factoryCreator);
    }

    public static SettingsIntegerNumber random(final SettingsRandomType type, final int min, final int max) {
        IntegerFactoryCreator factoryCreator = sp -> IntegerFactory.createRandom(sp.getRandomSupport(type), min, max);

        return new SettingsIntegerNumber(factoryCreator);
    }

    IntegerFactory createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface IntegerFactoryCreator {
        IntegerFactory create(SettingsParallelism parallelism);
    }
}
