package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.profile.factory.RandomEnumFactoryProfile;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.common.profile.DefaultObjectProfile;
import com.dipasquale.common.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumValue<T extends Enum<T>> {
    private final ObjectProfileFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> EnumValue<T> literal(final T value) {
        ObjectProfileFactoryCreator<T> factoryCreator = p -> new DefaultObjectProfile<>(p.isEnabled(), new LiteralEnumFactory<>(value));

        return new EnumValue<>(factoryCreator);
    }

    private static <T extends Enum<T>> EnumValue<T> createRandom(final RandomType type, final T[] values) {
        ObjectProfileFactoryCreator<T> factoryCreator = p -> new RandomEnumFactoryProfile<>(p.isEnabled(), type, values);

        return new EnumValue<>(factoryCreator);
    }

    public static <T extends Enum<T>> EnumValue<T> random(final Class<T> type) {
        return createRandom(RandomType.UNIFORM, type.getEnumConstants());
    }

    public static <T extends Enum<T>> EnumValue<T> random(final T... values) {
        return createRandom(RandomType.UNIFORM, values);
    }

    ObjectProfile<EnumFactory<T>> createFactoryProfile(final ParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectProfileFactoryCreator<T extends Enum<T>> {
        ObjectProfile<EnumFactory<T>> create(ParallelismSupport parallelism);
    }
}
