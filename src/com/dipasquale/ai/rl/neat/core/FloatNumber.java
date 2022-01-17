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

    static <T extends FloatFactory & DualModeObject> DualModeFactory createFactoryAdapter(final T floatFactory) {
        return new DualModeFactoryAdapter<>(floatFactory);
    }

    public static FloatNumber literal(final float value) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeFloatFactory floatFactory = new DualModeFloatFactory(initializationContext.getConcurrencyLevel(), new LiteralFloatFactory(value));

            return createFactoryAdapter(floatFactory);
        };

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float minimum, final float maximum) {
        DualModeFactoryCreator factoryCreator = initializationContext -> {
            DualModeBoundedRandomFloatFactory floatFactory = new DualModeBoundedRandomFloatFactory(initializationContext.createRandomSupport(type), minimum, maximum);

            return createFactoryAdapter(floatFactory);
        };

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float range) {
        return random(type, -range, range);
    }

    DualModeFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    interface DualModeFactory extends FloatFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeFactoryAdapter<T extends FloatFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T floatFactory;

        @Override
        public float create() {
            return floatFactory.create();
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
