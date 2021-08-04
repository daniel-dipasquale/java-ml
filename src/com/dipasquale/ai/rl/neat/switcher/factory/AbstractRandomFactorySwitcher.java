package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;

import java.io.Serial;

abstract class AbstractRandomFactorySwitcher<T> extends AbstractObjectSwitcher<T> {
    @Serial
    private static final long serialVersionUID = 4692957818707705539L;
    private final RandomType type;

    protected AbstractRandomFactorySwitcher(final boolean isOn, final RandomType type) {
        super(isOn);
        this.type = type;
    }

    protected RandomSupport getRandomSupport(final boolean isOn) {
        return Constants.getRandomSupport(type, isOn);
    }
}