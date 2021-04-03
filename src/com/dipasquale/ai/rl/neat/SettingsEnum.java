package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.EnumFactory;
import com.dipasquale.common.RandomSupportFloat;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsEnum<T extends Enum<T>> {
    private final EnumFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> SettingsEnum<T> literal(final T value) {
        return new SettingsEnum<>(sp -> new LiteralEnumFactory<>(value));
    }

    private static <T extends Enum<T>> SettingsEnum<T> createRandom(final T[] constants) {
        List<T> values = Lists.newArrayList(constants);

        return new SettingsEnum<>(sp -> new RandomEnumFactory<>(sp.getRandomSupport(SettingsRandomType.UNIFORM), values));
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final Class<T> type) {
        return createRandom(type.getEnumConstants());
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final T... constants) {
        return createRandom(constants);
    }

    EnumFactory<T> createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    interface EnumFactoryCreator<T extends Enum<T>> {
        EnumFactory<T> create(SettingsParallelism parallelism);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LiteralEnumFactory<T extends Enum<T>> implements EnumFactory<T> {
        private final T value;

        @Override
        public T create() {
            return value;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class RandomEnumFactory<T extends Enum<T>> implements EnumFactory<T> {
        private final RandomSupportFloat randomSupport;
        private final List<T> values;

        @Override
        public T create() {
            int index = randomSupport.next(0, values.size());

            return values.get(index);
        }
    }
}
