package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class RandomFloatFactorySwitcher extends AbstractObjectSwitcher<FloatFactory> {
    @Serial
    private static final long serialVersionUID = 796774320554634960L;

    public RandomFloatFactorySwitcher(final boolean isOn, final RandomType type) {
        super(isOn, new DefaultFloatFactory(true, type), new DefaultFloatFactory(false, type));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final boolean isOn;
        private final RandomType type;

        @Override
        public float create() {
            return Constants.getRandomSupport(type, isOn).next();
        }
    }
}
