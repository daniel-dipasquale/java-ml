package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IntegerFactory;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsIntegerNumber {
    private final IntegerFactoryCreator factoryCreator;

    public static SettingsIntegerNumber literal(final int number) {
        return new SettingsIntegerNumber(sp -> new LiteralIntegerFactory(number));
    }

    public static SettingsIntegerNumber random(final SettingsRandomType type, final int min, final int max) {
        return new SettingsIntegerNumber(sp -> new RandomIntegerFactory(sp.getRandomSupport(type), min, max));
    }

    IntegerFactory createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface IntegerFactoryCreator {
        IntegerFactory create(SettingsParallelism parallelism);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LiteralIntegerFactory implements IntegerFactory {
        private final int number;

        @Override
        public int create() {
            return number;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class RandomIntegerFactory implements IntegerFactory {
        private final RandomSupportFloat randomSupport;
        private final int min;
        private final int max;

        @Override
        public int create() {
            return randomSupport.next(min, max);
        }
    }
}
