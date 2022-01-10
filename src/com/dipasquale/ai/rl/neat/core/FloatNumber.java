package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeBoundedRandomFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeFloatFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FloatNumber {
    private final DualModeFactoryCreator factoryCreator;
    private final Object singletonKey = new Object();

    public static <T extends FloatFactory & DualModeObject> DualModeFactory createFactory(final T floatFactory) {
        return new InternalDualModeFactory<>(floatFactory);
    }

    public static FloatNumber literal(final float value) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeFloatFactory floatFactory = new DualModeFloatFactory(initializationContext.getConcurrencyLevel(), new LiteralFloatFactory(value));

            return createFactory(floatFactory);
        };

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float min, final float max) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeBoundedRandomFloatFactory floatFactory = new DualModeBoundedRandomFloatFactory(initializationContext.createRandomSupport(type), min, max);

            return createFactory(floatFactory);
        };

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float range) {
        return random(type, -range, range);
    }

    public DualModeFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    public float getSingletonValue(final InitializationContext initializationContext) {
        if (!initializationContext.getContainer().containsKey(singletonKey)) {
            float singleton = factoryCreator.create(initializationContext).create();

            initializationContext.getContainer().setValue(singletonKey, singleton);

            return singleton;
        }

        return (float) initializationContext.getContainer().getValue(singletonKey);
    }

    public interface DualModeFactory extends FloatFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalDualModeFactory<T extends FloatFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T floatFactory;

        @Override
        public float create() {
            return floatFactory.create();
        }

        @Override
        public int concurrencyLevel() {
            return floatFactory.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            floatFactory.activateMode(concurrencyLevel);
        }
    }

    @FunctionalInterface
    private interface DualModeFactoryCreator {
        DualModeFactory create(InitializationContext initializationContext);
    }
}
