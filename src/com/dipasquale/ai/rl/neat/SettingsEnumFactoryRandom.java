package com.dipasquale.ai.rl.neat;

import com.dipasquale.concurrent.EnumBiFactory;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsEnumFactoryRandom<T extends Enum<T>> implements EnumBiFactory<T> {
    @Serial
    private static final long serialVersionUID = 2087565933469556834L;
    private final SettingsRandomType type;
    private final List<? extends T> values;
    private final SettingsEnumFactoryRandomContended contendedFactory = new SettingsEnumFactoryRandomContended();

    private T create(final boolean contended) {
        RandomSupportFloat randomSupport = SettingsConstants.getRandomSupport(type, contended);
        int index = randomSupport.next(0, values.size());

        return values.get(index);
    }

    @Override
    public T create() {
        return create(false);
    }

    @Override
    public EnumBiFactory<T> selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedFactory;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class SettingsEnumFactoryRandomContended implements EnumBiFactory<T> {
        @Serial
        private static final long serialVersionUID = 1102884778826767986L;

        @Override
        public T create() {
            return SettingsEnumFactoryRandom.this.create(true);
        }

        @Override
        public EnumBiFactory<T> selectContended(final boolean contended) {
            return SettingsEnumFactoryRandom.this.selectContended(contended);
        }
    }
}
