package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeBoundedRandomIntegerFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeIntegerFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final DualModeFactoryCreator factoryCreator;

    static <T extends IntegerFactory & DualModeObject> DualModeFactory createFactoryAdapter(final T integerFactory) {
        return new DualModeFactoryAdapter<>(integerFactory);
    }

    public static IntegerNumber literal(final int value) {
        return IntegerNumber.builder()
                .factoryCreator(initializationContext -> {
                    DualModeIntegerFactory integerFactory = new DualModeIntegerFactory(initializationContext.getConcurrencyLevel(), new LiteralIntegerFactory(value));

                    return createFactoryAdapter(integerFactory);
                })
                .build();
    }

    public static IntegerNumber random(final RandomType type, final int minimum, final int maximum) {
        return IntegerNumber.builder()
                .factoryCreator(initializationContext -> {
                    DualModeBoundedRandomIntegerFactory integerFactory = new DualModeBoundedRandomIntegerFactory(initializationContext.createRandomSupport(type), minimum, maximum);

                    return createFactoryAdapter(integerFactory);
                })
                .build();
    }

    DualModeFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    interface DualModeFactory extends IntegerFactory, DualModeObject {
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeFactoryAdapter<T extends IntegerFactory & DualModeObject> implements DualModeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 3324024183810540982L;
        private final T integerFactory;

        @Override
        public int create() {
            return integerFactory.create();
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
