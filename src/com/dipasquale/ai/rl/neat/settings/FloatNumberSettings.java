package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.switcher.factory.BoundedRandomFloatFactorySwitcher;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.LiteralFloatFactory;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FloatNumberSettings {
    private final ObjectSwitcherFactoryCreator factoryCreator;

    public static FloatNumberSettings literal(final float value) {
        ObjectSwitcherFactoryCreator factoryCreator = p -> new DefaultObjectSwitcher<>(p.isEnabled(), new LiteralFloatFactory(value));

        return new FloatNumberSettings(factoryCreator);
    }

    public static FloatNumberSettings random(final RandomType type, final float min, final float max) {
        ObjectSwitcherFactoryCreator factoryCreator = p -> new BoundedRandomFloatFactorySwitcher(p.isEnabled(), type, min, max);

        return new FloatNumberSettings(factoryCreator);
    }

    ObjectSwitcher<FloatFactory> createFactorySwitcher(final ParallelismSupportSettings parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectSwitcherFactoryCreator {
        ObjectSwitcher<FloatFactory> create(ParallelismSupportSettings parallelism);
    }
}
