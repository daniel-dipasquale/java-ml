package com.dipasquale.synchronization.dual.mode.random.float1;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeRandomSupport implements RandomSupport, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 8926290819251751512L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final RandomSupport concurrentRandomSupport;
    private final RandomSupport defaultRandomSupport;

    public DualModeRandomSupport(final int concurrencyLevel, final RandomSupport concurrentRandomSupport, final RandomSupport defaultRandomSupport) {
        this.concurrencyLevelState = new ConcurrencyLevelState(concurrencyLevel);
        this.concurrentRandomSupport = concurrentRandomSupport;
        this.defaultRandomSupport = defaultRandomSupport;
    }

    @Override
    public float next() {
        if (concurrencyLevelState.getCurrent() > 0) {
            return concurrentRandomSupport.next();
        }

        return defaultRandomSupport.next();
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
