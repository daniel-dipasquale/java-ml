package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsFloatNumber {
    private final FloatFactoryCreator factoryCreator;

    static SettingsFloatNumber strategy(final FloatFactoryCreator factoryCreator) {
        return new SettingsFloatNumber(factoryCreator);
    }

    public static SettingsFloatNumber literal(final float number) {
        FloatFactoryCreator factoryCreator = sp -> new LiteralFloatFactory(number);

        return strategy(factoryCreator);
    }

    public static SettingsFloatNumber random(final SettingsRandomType type, final float min, final float max) {
        FloatFactoryCreator factoryCreator = sp -> new RandomFloatFactory(sp.getRandomSupport(type), min, max);

        return strategy(factoryCreator);
    }

    FloatFactory createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    interface FloatFactoryCreator {
        FloatFactory create(SettingsParallelism parallelism);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LiteralFloatFactory implements FloatFactory {
        private final float number;

        @Override
        public float create() {
            return number;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class RandomFloatFactory implements FloatFactory {
        private final RandomSupportFloat randomSupport;
        private final float min;
        private final float max;

        @Override
        public float create() {
            return randomSupport.next(min, max);
        }
    }
}
