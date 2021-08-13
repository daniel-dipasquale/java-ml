/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;

import java.io.Serial;

public final class RandomSupportFactorySwitcher extends AbstractObjectSwitcher<RandomSupport> {
    @Serial
    private static final long serialVersionUID = -5824135271694994241L;
    private final RandomType type;

    public RandomSupportFactorySwitcher(final boolean isOn, final RandomType type) {
        super(isOn, null, null);
        this.type = type;
    }

    @Override
    protected RandomSupport getOnObject() {
        return Constants.getRandomSupport(type, true);
    }

    @Override
    protected RandomSupport getOffObject() {
        return Constants.getRandomSupport(type, false);
    }
}
