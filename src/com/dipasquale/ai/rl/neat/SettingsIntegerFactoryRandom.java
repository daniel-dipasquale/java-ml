package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.random.RandomSupportFloat;
import com.dipasquale.common.concurrent.IntegerBiFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsIntegerFactoryRandom implements IntegerBiFactory {
    @Serial
    private static final long serialVersionUID = -1387158511021812055L;
    private final SettingsRandomType type;
    private final int min;
    private final int max;
    private final IntegerBiFactoryRandomContended contendedFactory = new IntegerBiFactoryRandomContended();

    private int create(final boolean contended) {
        RandomSupportFloat randomSupport = SettingsConstants.getRandomSupport(type, contended);

        return randomSupport.next(min, max);
    }

    @Override
    public int create() {
        return create(false);
    }

    @Override
    public IntegerBiFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedFactory;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class IntegerBiFactoryRandomContended implements IntegerBiFactory {
        @Serial
        private static final long serialVersionUID = 7913844336205474062L;

        @Override
        public int create() {
            return SettingsIntegerFactoryRandom.this.create(true);
        }

        @Override
        public IntegerBiFactory selectContended(final boolean contended) {
            return SettingsIntegerFactoryRandom.this.selectContended(contended);
        }
    }
}
