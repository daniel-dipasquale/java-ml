package com.dipasquale.synchronization.dual.mode.random;

import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeRandomSupport implements RandomSupport, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 8926290819251751512L;
    private final RandomSupport concurrentRandomSupport;
    private final RandomSupport defaultRandomSupport;
    private RandomSupport selectedRandomSupport;

    public DualModeRandomSupport(final int concurrencyLevel, final RandomSupport concurrentRandomSupport, final RandomSupport defaultRandomSupport) {
        this.concurrentRandomSupport = concurrentRandomSupport;
        this.defaultRandomSupport = defaultRandomSupport;
        this.selectedRandomSupport = select(concurrencyLevel, concurrentRandomSupport, defaultRandomSupport);
    }

    private static RandomSupport select(final int concurrencyLevel, final RandomSupport concurrentRandomSupport, final RandomSupport defaultRandomSupport) {
        if (concurrencyLevel > 0) {
            return concurrentRandomSupport;
        }

        return defaultRandomSupport;
    }

    @Override
    public float nextFloat() {
        return selectedRandomSupport.nextFloat();
    }

    @Override
    public double nextDouble() {
        return selectedRandomSupport.nextDouble();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        selectedRandomSupport = select(concurrencyLevel, concurrentRandomSupport, defaultRandomSupport);
    }
}
