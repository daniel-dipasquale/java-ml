package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.concurrent.EnumBiFactory;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsEnum<T extends Enum<T>> {
    private final EnumFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> SettingsEnum<T> literal(final T value) {
        EnumFactoryCreator<T> factoryCreator = sp -> EnumBiFactory.createLiteral(value);

        return new SettingsEnum<>(factoryCreator);
    }

    private static <T extends Enum<T>> SettingsEnum<T> createRandom(final T[] values) {
        EnumFactoryCreator<T> factoryCreator = sp -> {
            EnumBiFactory<T> factory = new SettingsEnumFactoryRandom<>(SettingsRandomType.UNIFORM, Lists.newArrayList(values));

            return factory.selectContended(sp.isEnabled());
        };

        return new SettingsEnum<>(factoryCreator);
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final Class<T> type) {
        return createRandom(type.getEnumConstants());
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final T... values) {
        return createRandom(values);
    }

    EnumBiFactory<T> createFactory(final SettingsParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface EnumFactoryCreator<T extends Enum<T>> {
        EnumBiFactory<T> create(SettingsParallelismSupport parallelism);
    }
}
