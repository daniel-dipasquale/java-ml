package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.EnumFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsEnumFactoryRandom<T extends Enum<T>> implements EnumFactory<T> {
    @Serial
    private static final long serialVersionUID = 2087565933469556834L;
    private final SettingsRandomType type;
    private final List<? extends T> values;
    private final SettingsEnumFactoryRandomContended factoryContended = new SettingsEnumFactoryRandomContended();

    @Override
    public T create() {
        int index = SettingsConstants.getRandomSupport(type, false).next(0, values.size());

        return values.get(index);
    }

    @Override
    public EnumFactory<T> selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return factoryContended;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class SettingsEnumFactoryRandomContended implements EnumFactory<T> {
        @Serial
        private static final long serialVersionUID = 1102884778826767986L;

        @Override
        public T create() {
            int index = SettingsConstants.getRandomSupport(type, true).next(0, values.size());

            return values.get(index);
        }

        @Override
        public EnumFactory<T> selectContended(final boolean contended) {
            return SettingsEnumFactoryRandom.this.selectContended(contended);
        }
    }
}
