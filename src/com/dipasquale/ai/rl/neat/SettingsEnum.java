package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.EnumFactory;
import com.dipasquale.common.RandomSupportFloat;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsEnum<T extends Enum<T>> {
    private final EnumFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> SettingsEnum<T> literal(final T value) {
        EnumFactoryCreator<T> factoryCreator = sp -> EnumFactory.createLiteral(value);

        return new SettingsEnum<>(factoryCreator);
    }

    private static <T extends Enum<T>> SettingsEnum<T> createRandom(final T[] values) {
        EnumFactoryCreator<T> factoryCreator = sp -> {
            RandomSupportFloat randomSupport = sp.getRandomSupport(SettingsRandomType.UNIFORM);

            return EnumFactory.createRandom(randomSupport, Lists.newArrayList(values));
        };

        return new SettingsEnum<>(factoryCreator);
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final Class<T> type) {
        return createRandom(type.getEnumConstants());
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final T... values) {
        return createRandom(values);
    }

    EnumFactory<T> createFactory(final SettingsParallelism parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface EnumFactoryCreator<T extends Enum<T>> {
        EnumFactory<T> create(SettingsParallelism parallelism);
    }
}
