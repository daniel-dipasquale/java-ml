package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.BoundedRandomIntegerFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final IntegerFactoryCreator factoryCreator;

    public static IntegerNumber literal(final int value) {
        return IntegerNumber.builder()
                .factoryCreator(initializationContext -> new LiteralIntegerFactory(value))
                .build();
    }

    public static IntegerNumber random(final RandomType type, final int minimum, final int maximum) {
        return IntegerNumber.builder()
                .factoryCreator(initializationContext -> {
                    RandomSupport randomSupport = initializationContext.createRandomSupport(type);

                    return new BoundedRandomIntegerFactory(randomSupport, minimum, maximum);
                })
                .build();
    }

    IntegerFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    @FunctionalInterface
    private interface IntegerFactoryCreator {
        IntegerFactory create(InitializationContext initializationContext);
    }
}
