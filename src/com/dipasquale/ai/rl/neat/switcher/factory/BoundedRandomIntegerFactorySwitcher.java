/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomIntegerFactorySwitcher extends AbstractObjectSwitcher<IntegerFactory> {
    @Serial
    private static final long serialVersionUID = 3322232093472816035L;

    public BoundedRandomIntegerFactorySwitcher(final boolean isOn, final RandomType type, final int min, final int max) {
        super(isOn, new DefaultIntegerFactory(true, type, min, max), new DefaultIntegerFactory(false, type, min, max));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultIntegerFactory implements IntegerFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final boolean isOn;
        private final RandomType type;
        private final int min;
        private final int max;

        @Override
        public int create() {
            return Constants.getRandomSupport(type, isOn).next(min, max);
        }
    }
}
