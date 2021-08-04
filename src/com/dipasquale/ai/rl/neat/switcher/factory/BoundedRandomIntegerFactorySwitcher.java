package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.IntegerFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomIntegerFactorySwitcher extends AbstractRandomFactorySwitcher<IntegerFactory> {
    @Serial
    private static final long serialVersionUID = 3322232093472816035L;
    private final int min;
    private final int max;
    @Getter(AccessLevel.PROTECTED)
    private final IntegerFactory on;
    @Getter(AccessLevel.PROTECTED)
    private final IntegerFactory off;

    public BoundedRandomIntegerFactorySwitcher(final boolean isOn, final RandomType type, final int min, final int max) {
        super(isOn, type);
        this.min = min;
        this.max = max;
        this.on = new DefaultIntegerFactory(true);
        this.off = new DefaultIntegerFactory(false);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class DefaultIntegerFactory implements IntegerFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final boolean isOn;

        @Override
        public int create() {
            return getRandomSupport(isOn).next(min, max);
        }
    }
}
