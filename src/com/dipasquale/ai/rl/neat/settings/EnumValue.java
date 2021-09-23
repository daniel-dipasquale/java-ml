package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.RandomEnumFactoryProfile;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumValue<T extends Enum<T>> {
    private final ObjectProfileFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> EnumValue<T> literal(final T value) {
        ObjectProfileFactoryCreator<T> factoryCreator = ps -> new DefaultObjectProfile<>(ps.isEnabled(), new LiteralEnumFactory<>(value));

        return new EnumValue<>(factoryCreator);
    }

    private static <T extends Enum<T>> EnumValue<T> createRandom(final RandomType type, final T[] values) {
        ObjectProfileFactoryCreator<T> factoryCreator = ps -> new RandomEnumFactoryProfile<>(ps.isEnabled(), type, values);

        return new EnumValue<>(factoryCreator);
    }

    public static <T extends Enum<T>> EnumValue<T> random(final Class<T> type) {
        return createRandom(RandomType.UNIFORM, type.getEnumConstants());
    }

    public static <T extends Enum<T>> EnumValue<T> random(final T... values) {
        return createRandom(RandomType.UNIFORM, values);
    }

    ObjectProfile<EnumFactory<T>> createFactoryProfile(final ParallelismSupport parallelismSupport) {
        return factoryCreator.create(parallelismSupport);
    }

    @FunctionalInterface
    private interface ObjectProfileFactoryCreator<T extends Enum<T>> {
        ObjectProfile<EnumFactory<T>> create(ParallelismSupport parallelismSupport);
    }
}
