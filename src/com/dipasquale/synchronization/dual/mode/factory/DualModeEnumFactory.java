package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeEnumFactory<T extends Enum<T>> implements EnumFactory<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 3118010100231281879L;
    private final EnumFactory<T> concurrentEnumFactory;
    private final EnumFactory<T> defaultEnumFactory;
    private EnumFactory<T> selectedEnumFactory;

    public DualModeEnumFactory(final int concurrencyLevel, final EnumFactory<T> concurrentEnumFactory, final EnumFactory<T> defaultEnumFactory) {
        this.concurrentEnumFactory = concurrentEnumFactory;
        this.defaultEnumFactory = defaultEnumFactory;
        this.selectedEnumFactory = select(concurrencyLevel, concurrentEnumFactory, defaultEnumFactory);
    }

    public DualModeEnumFactory(final int concurrencyLevel, final EnumFactory<T> enumFactory) {
        this(concurrencyLevel, enumFactory, enumFactory);
    }

    private static <T extends Enum<T>> EnumFactory<T> select(final int concurrencyLevel, final EnumFactory<T> concurrentEnumFactory, final EnumFactory<T> defaultEnumFactory) {
        if (concurrencyLevel > 0) {
            return concurrentEnumFactory;
        }

        return defaultEnumFactory;
    }

    @Override
    public T create() {
        return selectedEnumFactory.create();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        selectedEnumFactory = select(concurrencyLevel, concurrentEnumFactory, defaultEnumFactory);
    }
}
