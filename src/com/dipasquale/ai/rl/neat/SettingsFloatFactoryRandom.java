package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsFloatFactoryRandom implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 2597747301977413978L;
    private final SettingsRandomType type;
    private final float min;
    private final float max;
    private final FloatFactoryRandomContended factoryContended = new FloatFactoryRandomContended();

    @Override
    public float create() {
        return SettingsConstants.getRandomSupport(type, false).next(min, max);
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return factoryContended;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class FloatFactoryRandomContended implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 1102884778826767986L;

        @Override
        public float create() {
            return SettingsConstants.getRandomSupport(type, true).next(min, max);
        }

        @Override
        public FloatFactory selectContended(final boolean contended) {
            return SettingsFloatFactoryRandom.this.selectContended(contended);
        }
    }
}
