package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.BoundedRandomFloatFactoryProfile;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FloatNumber {
    private final ObjectProfileFactoryCreator factoryCreator;

    public static FloatNumber literal(final float value) {
        ObjectProfileFactoryCreator factoryCreator = p -> new DefaultObjectProfile<>(p.isEnabled(), new LiteralFloatFactory(value));

        return new FloatNumber(factoryCreator);
    }

    public static FloatNumber random(final RandomType type, final float min, final float max) {
        ObjectProfileFactoryCreator factoryCreator = p -> new BoundedRandomFloatFactoryProfile(p.isEnabled(), type, min, max);

        return new FloatNumber(factoryCreator);
    }

    ObjectProfile<FloatFactory> createFactoryProfile(final ParallelismSupport parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectProfileFactoryCreator {
        ObjectProfile<FloatFactory> create(ParallelismSupport parallelism);
    }
}
