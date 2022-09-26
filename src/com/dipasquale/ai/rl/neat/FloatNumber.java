package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.BoundedRandomFloatFactory;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ConstantFloatFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class FloatNumber {
    private final FloatFactoryValidRange factoryValidRange;
    private final FloatFactoryCreator factoryCreator;

    public static FloatNumber constant(final float value) {
        return FloatNumber.builder()
                .factoryValidRange(new FloatFactoryValidRange(value, value))
                .factoryCreator(initializationContext -> new ConstantFloatFactory(value))
                .build();
    }

    public static FloatNumber random(final RandomType type, final float minimum, final float maximum) {
        return FloatNumber.builder()
                .factoryValidRange(new FloatFactoryValidRange(minimum, maximum))
                .factoryCreator(initializationContext -> {
                    RandomSupport randomSupport = initializationContext.createRandomSupport(type);

                    return new BoundedRandomFloatFactory(randomSupport, minimum, maximum);
                })
                .build();
    }

    public static FloatNumber random(final RandomType type, final float range) {
        return random(type, -range, range);
    }

    FloatFactory createFactory(final NeatInitializationContext initializationContext, final String name) {
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.minimum, factoryValidRange.maximum, name);

        return factoryCreator.create(initializationContext);
    }

    FloatFactory createFactory(final NeatInitializationContext initializationContext, final float minimum, final String name) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, minimum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, minimum, name);

        return createFactory(initializationContext, name);
    }

    FloatFactory createFactory(final NeatInitializationContext initializationContext, final float minimum, final float maximum, final String name) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, minimum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, minimum, name);
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, maximum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, maximum, name);

        return createFactory(initializationContext, name);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class FloatFactoryValidRange {
        private final float minimum;
        private final float maximum;
    }

    @FunctionalInterface
    private interface FloatFactoryCreator {
        FloatFactory create(NeatInitializationContext initializationContext);
    }
}
