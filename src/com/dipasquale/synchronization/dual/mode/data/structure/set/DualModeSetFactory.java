package com.dipasquale.synchronization.dual.mode.data.structure.set;

import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public final class DualModeSetFactory implements SetFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 1572849643654328225L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final DualModeFactory<SetFactory> setFactoryCreator;
    private SetFactory setFactory;

    private DualModeSetFactory(final ConcurrencyLevelState concurrencyLevelState, final DualModeFactory<SetFactory> setFactoryCreator) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.setFactoryCreator = setFactoryCreator;
        this.setFactory = setFactoryCreator.create(concurrencyLevelState);
    }

    public DualModeSetFactory(final int concurrencyLevel, final int maximumConcurrencyLevel, final DualModeFactory<SetFactory> setFactoryCreator) {
        this(new ConcurrencyLevelState(concurrencyLevel, maximumConcurrencyLevel), setFactoryCreator);
    }

    public DualModeSetFactory(final int concurrencyLevel, final int maximumConcurrencyLevel) {
        this(concurrencyLevel, maximumConcurrencyLevel, HashSetFactoryCreator.getInstance());
    }

    @Override
    public <T> Set<T> create(final Set<T> other) {
        return setFactory.create(other);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
        setFactory = setFactoryCreator.create(concurrencyLevelState);
    }
}
