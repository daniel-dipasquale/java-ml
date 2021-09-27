package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.BoundedRandomIntegerFactoryProfile;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final ObjectProfileFactoryCreator factoryCreator;
    private Integer singletonValue = null;

    public static IntegerNumber literal(final int value) {
        ObjectProfileFactoryCreator factoryCreator = ps -> new DefaultObjectProfile<>(ps.isEnabled(), new LiteralIntegerFactory(value));

        return new IntegerNumber(factoryCreator);
    }

    public static IntegerNumber random(final RandomType type, final int min, final int max) {
        ObjectProfileFactoryCreator factoryCreator = ps -> new BoundedRandomIntegerFactoryProfile(ps.isEnabled(), type, min, max);

        return new IntegerNumber(factoryCreator);
    }

    public ObjectProfile<IntegerFactory> createFactoryProfile(final ParallelismSupport parallelismSupport) {
        return factoryCreator.create(parallelismSupport);
    }

    public int getSingletonValue(final ParallelismSupport parallelismSupport) {
        if (singletonValue == null) {
            ObjectProfile<IntegerFactory> factoryProfile = factoryCreator.create(parallelismSupport);

            singletonValue = factoryProfile.getObject().create();
        }

        return singletonValue;
    }

    @FunctionalInterface
    private interface ObjectProfileFactoryCreator {
        ObjectProfile<IntegerFactory> create(ParallelismSupport parallelismSupport);
    }
}
