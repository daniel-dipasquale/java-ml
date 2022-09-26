package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.BoundedRandomIntegerFactory;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ConstantIntegerFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final IntegerFactoryValidRange factoryValidRange;
    private final IntegerFactoryCreator factoryCreator;

    public static IntegerNumber constant(final int value) {
        return IntegerNumber.builder()
                .factoryValidRange(new IntegerFactoryValidRange(value, value))
                .factoryCreator(initializationContext -> new ConstantIntegerFactory(value))
                .build();
    }

    public static IntegerNumber random(final RandomType type, final int minimum, final int maximum) {
        return IntegerNumber.builder()
                .factoryValidRange(new IntegerFactoryValidRange(minimum, maximum))
                .factoryCreator(initializationContext -> {
                    RandomSupport randomSupport = initializationContext.createRandomSupport(type);

                    return new BoundedRandomIntegerFactory(randomSupport, minimum, maximum);
                })
                .build();
    }

    IntegerFactory createFactory(final NeatInitializationContext initializationContext, final String name) {
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.minimum, factoryValidRange.maximum, name);

        return factoryCreator.create(initializationContext);
    }

    IntegerFactory createFactory(final NeatInitializationContext initializationContext, final int minimum, final String name) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, minimum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, minimum, name);

        return createFactory(initializationContext, name);
    }

    IntegerFactory createFactory(final NeatInitializationContext initializationContext, final int minimum, final int maximum, final String name) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, minimum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, minimum, name);
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(factoryValidRange.minimum, maximum, name);
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(factoryValidRange.maximum, maximum, name);

        return createFactory(initializationContext, name);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class IntegerFactoryValidRange {
        private final int minimum;
        private final int maximum;
    }

    @FunctionalInterface
    private interface IntegerFactoryCreator {
        IntegerFactory create(NeatInitializationContext initializationContext);
    }
}
