package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.profile.factory.BoundedRandomIntegerFactoryProfile;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.common.profile.DefaultObjectProfile;
import com.dipasquale.common.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerNumber {
    private final ObjectProfileFactoryCreator factoryCreator;

    public static IntegerNumber literal(final int value) {
        ObjectProfileFactoryCreator factoryCreator = p -> new DefaultObjectProfile<>(p.isEnabled(), new LiteralIntegerFactory(value));

        return new IntegerNumber(factoryCreator);
    }

    public static IntegerNumber random(final RandomType type, final int min, final int max) {
        ObjectProfileFactoryCreator factoryCreator = p -> new BoundedRandomIntegerFactoryProfile(p.isEnabled(), type, min, max);

        return new IntegerNumber(factoryCreator);
    }

    ObjectProfile<IntegerFactory> createFactoryProfile(final ParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectProfileFactoryCreator {
        ObjectProfile<IntegerFactory> create(ParallelismSupport parallelism);
    }
}
