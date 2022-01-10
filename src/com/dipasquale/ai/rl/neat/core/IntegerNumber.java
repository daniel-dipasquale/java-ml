package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeBoundedRandomIntegerFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeIntegerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final DualModeFactoryCreator factoryCreator;
    private final Object singletonKey = new Object();

    public static <T extends IntegerFactory & DualModeObject> DualModeFactory createFactory(final T integerFactory) {
        return new InternalDualModeFactory<>(integerFactory);
    }

    public static IntegerNumber literal(final int value) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeIntegerFactory integerFactory = new DualModeIntegerFactory(initializationContext.getConcurrencyLevel(), new LiteralIntegerFactory(value));

            return createFactory(integerFactory);
        };

        return new IntegerNumber(factoryCreator);
    }

    public static IntegerNumber random(final RandomType type, final int min, final int max) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeBoundedRandomIntegerFactory integerFactory = new DualModeBoundedRandomIntegerFactory(initializationContext.createRandomSupport(type), min, max);

            return createFactory(integerFactory);
        };

        return new IntegerNumber(factoryCreator);
    }

    public DualModeFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    public int getSingletonValue(final InitializationContext initializationContext) {
        if (!initializationContext.getContainer().containsKey(singletonKey)) {
            int singleton = factoryCreator.create(initializationContext).create();

            initializationContext.getContainer().setValue(singletonKey, singleton);

            return singleton;
        }

        return (int) initializationContext.getContainer().getValue(singletonKey);
    }

    public interface DualModeFactory extends IntegerFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalDualModeFactory<T extends IntegerFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T integerFactory;

        @Override
        public int create() {
            return integerFactory.create();
        }

        @Override
        public int concurrencyLevel() {
            return integerFactory.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            integerFactory.activateMode(concurrencyLevel);
        }
    }

    @FunctionalInterface
    private interface DualModeFactoryCreator {
        DualModeFactory create(InitializationContext initializationContext);
    }
}
