package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.switcher.factory.BoundedRandomIntegerFactorySwitcher;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.LiteralIntegerFactory;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerNumberSettings {
    private final ObjectSwitcherFactoryCreator factoryCreator;

    public static IntegerNumberSettings literal(final int value) {
        ObjectSwitcherFactoryCreator factoryCreator = p -> new DefaultObjectSwitcher<>(p.isEnabled(), new LiteralIntegerFactory(value));

        return new IntegerNumberSettings(factoryCreator);
    }

    public static IntegerNumberSettings random(final RandomType type, final int min, final int max) {
        ObjectSwitcherFactoryCreator factoryCreator = p -> new BoundedRandomIntegerFactorySwitcher(p.isEnabled(), type, min, max);

        return new IntegerNumberSettings(factoryCreator);
    }

    ObjectSwitcher<IntegerFactory> createFactorySwitcher(final ParallelismSupportSettings parallelism) {
        return factoryCreator.create(parallelism);
    }

    @FunctionalInterface
    private interface ObjectSwitcherFactoryCreator {
        ObjectSwitcher<IntegerFactory> create(ParallelismSupportSettings parallelism);
    }
}
