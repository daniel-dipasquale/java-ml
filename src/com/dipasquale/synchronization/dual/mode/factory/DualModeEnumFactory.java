package com.dipasquale.synchronization.dual.mode.factory;

import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeEnumFactory<T extends Enum<T>> implements EnumFactory<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 3118010100231281879L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final EnumFactory<T> concurrentEnumFactory;
    private final EnumFactory<T> defaultEnumFactory;

    public DualModeEnumFactory(final int concurrencyLevel, final EnumFactory<T> concurrentEnumFactory, final EnumFactory<T> defaultEnumFactory) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentEnumFactory = concurrentEnumFactory;
        this.defaultEnumFactory = defaultEnumFactory;
    }

    public DualModeEnumFactory(final int concurrencyLevel, final Pair<EnumFactory<T>> enumFactoryPair) {
        this(concurrencyLevel, enumFactoryPair.getLeft(), enumFactoryPair.getRight());
    }

    public DualModeEnumFactory(final int concurrencyLevel, final EnumFactory<T> enumFactory) {
        this(concurrencyLevel, enumFactory, enumFactory);
    }

    @Override
    public T create() {
        if (concurrencyLevelState.getCurrent() > 0) {
            return concurrentEnumFactory.create();
        }

        return defaultEnumFactory.create();
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
    }
}
