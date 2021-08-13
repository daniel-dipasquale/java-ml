/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomFloatFactorySwitcher extends AbstractObjectSwitcher<FloatFactory> {
    @Serial
    private static final long serialVersionUID = -791004180256061334L;

    public BoundedRandomFloatFactorySwitcher(final boolean isOn, final RandomType type, final float min, final float max) {
        super(isOn, new DefaultFloatFactory(true, type, min, max), new DefaultFloatFactory(false, type, min, max));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final boolean isOn;
        private final RandomType type;
        private final float min;
        private final float max;

        @Override
        public float create() {
            return Constants.getRandomSupport(type, isOn).next(min, max);
        }
    }
}
