package com.dipasquale.ai.rl.neat.switcher;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.FloatFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomFloatFactorySwitcher extends AbstractRandomFactorySwitcher<FloatFactory> {
    @Serial
    private static final long serialVersionUID = -791004180256061334L;
    private final float min;
    private final float max;
    @Getter(AccessLevel.PROTECTED)
    private final FloatFactory on;
    @Getter(AccessLevel.PROTECTED)
    private final FloatFactory off;

    public BoundedRandomFloatFactorySwitcher(final boolean isOn, final RandomType type, final float min, final float max) {
        super(isOn, type);
        this.min = min;
        this.max = max;
        this.on = new DefaultFloatFactory(true);
        this.off = new DefaultFloatFactory(false);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final boolean isOn;

        @Override
        public float create() {
            return getRandomSupport(isOn).next(min, max);
        }
    }
}
