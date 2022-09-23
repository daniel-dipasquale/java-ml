package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.BoundedRandomFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class FloatNumber {
    private final FloatFactoryCreator factoryCreator;

    public static FloatNumber literal(final float value) {
        return FloatNumber.builder()
                .factoryCreator(initializationContext -> new LiteralFloatFactory(value))
                .build();
    }

    public static FloatNumber random(final RandomType type, final float minimum, final float maximum) {
        return FloatNumber.builder()
                .factoryCreator(initializationContext -> {
                    RandomSupport randomSupport = initializationContext.createRandomSupport(type);

                    return new BoundedRandomFloatFactory(randomSupport, minimum, maximum);
                })
                .build();
    }

    public static FloatNumber random(final RandomType type, final float range) {
        return random(type, -range, range);
    }

    FloatFactory createFactory(final InitializationContext initializationContext) {
        return factoryCreator.create(initializationContext);
    }

    @FunctionalInterface
    private interface FloatFactoryCreator {
        FloatFactory create(InitializationContext initializationContext);
    }
}
