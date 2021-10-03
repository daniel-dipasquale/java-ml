package com.dipasquale.synchronization.dual.mode.data.structure.deque;

import com.dipasquale.common.factory.data.structure.deque.DequeFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Deque;

public final class DualModeDequeFactory implements DequeFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -4899149396612782657L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final DualModeFactory<DequeFactory> dequeFactoryCreator;
    private DequeFactory dequeFactory;

    private DualModeDequeFactory(final ConcurrencyLevelState concurrencyLevelState, final DualModeFactory<DequeFactory> dequeFactoryCreator) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.dequeFactoryCreator = dequeFactoryCreator;
        this.dequeFactory = dequeFactoryCreator.create(concurrencyLevelState);
    }

    public DualModeDequeFactory(final int concurrencyLevel, final int maximumConcurrencyLevel, final DualModeFactory<DequeFactory> dequeFactoryCreator) {
        this(new ConcurrencyLevelState(concurrencyLevel, maximumConcurrencyLevel), dequeFactoryCreator);
    }

    public DualModeDequeFactory(final int concurrencyLevel, final int maximumConcurrencyLevel) {
        this(concurrencyLevel, maximumConcurrencyLevel, LinkedDequeFactoryCreator.getInstance());
    }

    @Override
    public <T> Deque<T> create(final Deque<T> other) {
        return dequeFactory.create(other);
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
        dequeFactory = dequeFactoryCreator.create(concurrencyLevelState);
    }
}
