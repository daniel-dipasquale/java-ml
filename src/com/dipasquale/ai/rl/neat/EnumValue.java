package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.RandomEnumFactory;
import com.dipasquale.ai.rl.neat.factory.SequentialEnumFactory;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.data.structure.collection.ListSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class EnumValue<T extends Enum<T>> {
    private final EnumFactoryCreator<T> factoryCreator;

    public static <T extends Enum<T>> EnumValue<T> literal(final T value) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> new LiteralEnumFactory<>(value))
                .build();
    }

    private static <T extends Enum<T>> EnumValue<T> createSequence(final List<T> values) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> new SequentialEnumFactory<>(values))
                .build();
    }

    public static <T extends Enum<T>> EnumValue<T> sequence(final Sequence<T> sequence) {
        return createSequence(sequence.getValues());
    }

    private static <T extends Enum<T>> EnumValue<T> createRandom(final T[] values) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> {
                    RandomSupport randomSupport = initializationContext.createDefaultRandomSupport();

                    return new RandomEnumFactory<>(randomSupport, ListSupport.create(values));
                })
                .build();
    }

    public static <T extends Enum<T>> EnumValue<T> randomAll(final Class<T> type) {
        return createRandom(type.getEnumConstants());
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumValue<T> random(final T... values) {
        return createRandom(values);
    }

    EnumFactory<T> createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    @FunctionalInterface
    private interface EnumFactoryCreator<T extends Enum<T>> {
        EnumFactory<T> create(InitializationContext initializationContext);
    }
}
