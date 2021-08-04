package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.RandomSupport;

import java.io.Serial;

public final class RandomSupportFactorySwitcher extends AbstractRandomFactorySwitcher<RandomSupport> {
    @Serial
    private static final long serialVersionUID = -5824135271694994241L;

    public RandomSupportFactorySwitcher(final boolean isOn, final RandomType type) {
        super(isOn, type);
    }

    @Override
    protected RandomSupport getOn() {
        return getRandomSupport(true);
    }

    @Override
    protected RandomSupport getOff() {
        return getRandomSupport(false);
    }
}
