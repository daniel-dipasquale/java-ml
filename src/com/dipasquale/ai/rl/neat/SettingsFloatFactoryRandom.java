package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
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
    private final FloatFactoryRandomContended contendedFactory = new FloatFactoryRandomContended();

    private float create(final boolean contended) {
        RandomSupportFloat randomSupport = SettingsConstants.getRandomSupport(type, contended);

        return randomSupport.next(min, max);
    }

    @Override
    public float create() {
        return create(false);
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedFactory;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class FloatFactoryRandomContended implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 1102884778826767986L;

        @Override
        public float create() {
            return SettingsFloatFactoryRandom.this.create(true);
        }

        @Override
        public FloatFactory selectContended(final boolean contended) {
            return SettingsFloatFactoryRandom.this.selectContended(contended);
        }
    }
}
