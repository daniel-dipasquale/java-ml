package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.random.RandomSupportFloat;
import com.dipasquale.concurrent.random.RandomBiSupportFloat;

import java.io.Serial;

final class SettingsRandomBiSupport implements RandomBiSupportFloat {
    @Serial
    private static final long serialVersionUID = 4147277962469700916L;
    private final SettingsRandomType type;
    private final SettingsRandomBiSupportContended randomBiSupportContended;

    SettingsRandomBiSupport(final SettingsRandomType type) {
        this.type = type;
        this.randomBiSupportContended = new SettingsRandomBiSupportContended();
    }

    private SettingsRandomBiSupport() {
        this(SettingsRandomType.UNIFORM);
    }

    private float next(final boolean contended) {
        RandomSupportFloat randomSupport = SettingsConstants.getRandomSupport(type, contended);

        return randomSupport.next();
    }

    @Override
    public float next() {
        return next(false);
    }

    @Override
    public RandomBiSupportFloat selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return randomBiSupportContended;
    }

    private final class SettingsRandomBiSupportContended implements RandomBiSupportFloat {
        @Serial
        private static final long serialVersionUID = 5518274131552811681L;

        @Override
        public float next() {
            return SettingsRandomBiSupport.this.next(true);
        }

        @Override
        public RandomBiSupportFloat selectContended(final boolean contended) {
            return SettingsRandomBiSupport.this.selectContended(contended);
        }
    }
}
