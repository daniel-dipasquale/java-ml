package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomEnumFactory;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.LiteralEnumFactory;
import com.dipasquale.data.structure.collection.Lists;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeEnumFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumValue<T extends Enum<T>> {
    private final DualModeFactoryCreator<T> factoryCreator;
    private final Object singletonKey = new Object();

    public static <TEnum extends Enum<TEnum>, TEnumFactory extends EnumFactory<TEnum> & DualModeObject> DualModeFactory<TEnum> createFactory(final TEnumFactory enumFactory) {
        return new InternalDualModeFactory<>(enumFactory);
    }

    public static <T extends Enum<T>> EnumValue<T> literal(final T value) {
        DualModeFactoryCreator<T> factoryCreator = ic -> createFactory(new DualModeEnumFactory<>(ic.getParallelism().getConcurrencyLevel(), new LiteralEnumFactory<>(value)));

        return new EnumValue<>(factoryCreator);
    }

    private static <T extends Enum<T>> EnumValue<T> createRandom(final RandomType type, final T[] values) {
        DualModeFactoryCreator<T> factoryCreator = ic -> createFactory(new DualModeRandomEnumFactory<>(ic.getRandomSupports().get(type), Lists.create(values)));

        return new EnumValue<>(factoryCreator);
    }

    public static <T extends Enum<T>> EnumValue<T> randomAll(final Class<T> type) {
        return createRandom(RandomType.UNIFORM, type.getEnumConstants());
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumValue<T> random(final T... values) {
        return createRandom(RandomType.UNIFORM, values);
    }

    public DualModeFactory<T> createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    public T getSingletonValue(final InitializationContext initializationContext) {
        if (!initializationContext.getContainer().containsKey(singletonKey)) {
            initializationContext.getContainer().setValue(singletonKey, factoryCreator.create(initializationContext).create());
        }

        return (T) initializationContext.getContainer().getValue(singletonKey);
    }

    public interface DualModeFactory<T extends Enum<T>> extends EnumFactory<T>, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalDualModeFactory<TEnum extends Enum<TEnum>, TEnumFactory extends EnumFactory<TEnum> & DualModeObject> implements DualModeFactory<TEnum>, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final TEnumFactory enumFactory;

        @Override
        public TEnum create() {
            return enumFactory.create();
        }

        @Override
        public int concurrencyLevel() {
            return enumFactory.concurrencyLevel();
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
