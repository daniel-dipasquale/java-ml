package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomEnumFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeSequentialEnumFactory;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.data.structure.collection.Lists;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeEnumFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class EnumValue<T extends Enum<T>> {
    private final DualModeFactoryCreator<T> factoryCreator;

    static <TEnum extends Enum<TEnum>, TEnumFactory extends EnumFactory<TEnum> & DualModeObject> DualModeFactory<TEnum> createFactoryAdapter(final TEnumFactory enumFactory) {
        return new DualModeFactoryAdapter<>(enumFactory);
    }

    public static <T extends Enum<T>> EnumValue<T> literal(final T value) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> {
                    DualModeEnumFactory<T> enumFactory = new DualModeEnumFactory<>(initializationContext.getConcurrencyLevel(), new LiteralEnumFactory<>(value));

                    return createFactoryAdapter(enumFactory);
                })
                .build();
    }

    private static <T extends Enum<T>> EnumValue<T> createSequence(final List<T> values) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> {
                    DualModeSequentialEnumFactory<T> enumFactory = new DualModeSequentialEnumFactory<>(initializationContext.getConcurrencyLevel(), values);

                    return createFactoryAdapter(enumFactory);
                })
                .build();
    }

    public static <T extends Enum<T>> EnumValue<T> sequence(final Sequence<T> sequence) {
        return createSequence(sequence.getValues());
    }

    private static <T extends Enum<T>> EnumValue<T> createRandom(final T[] values) {
        return EnumValue.<T>builder()
                .factoryCreator(initializationContext -> {
                    DualModeRandomEnumFactory<T> enumFactory = new DualModeRandomEnumFactory<>(initializationContext.createDefaultRandomSupport(), Lists.create(values));

                    return createFactoryAdapter(enumFactory);
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

    DualModeFactory<T> createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    interface DualModeFactory<T extends Enum<T>> extends EnumFactory<T>, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeFactoryAdapter<TEnum extends Enum<TEnum>, TEnumFactory extends EnumFactory<TEnum> & DualModeObject> implements DualModeFactory<TEnum>, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final TEnumFactory enumFactory;

        @Override
        public TEnum create() {
            return enumFactory.create();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            enumFactory.activateMode(concurrencyLevel);
        }
    }

    @FunctionalInterface
    private interface DualModeFactoryCreator<T extends Enum<T>> {
        DualModeFactory<T> create(InitializationContext initializationContext);
    }
}
