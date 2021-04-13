package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IntegerFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsIntegerFactoryRandom implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -1387158511021812055L;
    private final SettingsRandomType type;
    private final int min;
    private final int max;
    private final IntegerFactoryRandomContended factoryContended = new IntegerFactoryRandomContended();

    @Override
    public int create() {
        return SettingsConstants.getRandomSupport(type, false).next(min, max);
    }

    @Override
    public IntegerFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return factoryContended;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class IntegerFactoryRandomContended implements IntegerFactory {
        @Serial
        private static final long serialVersionUID = 7913844336205474062L;

        @Override
        public int create() {
            return SettingsConstants.getRandomSupport(type, true).next(min, max);
        }

        @Override
        public IntegerFactory selectContended(final boolean contended) {
            return SettingsIntegerFactoryRandom.this.selectContended(contended);
        }
    }
}
