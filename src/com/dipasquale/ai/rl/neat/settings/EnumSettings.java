package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.switcher.RandomEnumFactorySwitcher;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumSettings<T extends Enum<T>> {
    private final ObjectSwitcherFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> EnumSettings<T> literal(final T value) {
        ObjectSwitcherFactoryCreator<T> factoryCreator = p -> new DefaultObjectSwitcher<>(p.isEnabled(), new LiteralEnumFactory<>(value));

        return new EnumSettings<>(factoryCreator);
    }

    private static <T extends Enum<T>> EnumSettings<T> createRandom(final RandomType type, final T[] values) {
        ObjectSwitcherFactoryCreator<T> factoryCreator = p -> new RandomEnumFactorySwitcher<>(p.isEnabled(), type, values);

        return new EnumSettings<>(factoryCreator);
    }

    public static <T extends Enum<T>> EnumSettings<T> random(final Class<T> type) {
        return createRandom(RandomType.UNIFORM, type.getEnumConstants());
    }

    public static <T extends Enum<T>> EnumSettings<T> random(final T... values) {
        return createRandom(RandomType.UNIFORM, values);
    }

    ObjectSwitcher<EnumFactory<T>> createFactorySwitcher(final ParallelismSupportSettings parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectSwitcherFactoryCreator<T extends Enum<T>> {
        ObjectSwitcher<EnumFactory<T>> create(ParallelismSupportSettings parallelism);
    }
}
