package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeRandomFloatFactory implements FloatFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 796774320554634960L;
    private final DualModeRandomSupport randomSupport;

    @Override
    public float create() {
        return randomSupport.next();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        randomSupport.activateMode(concurrencyLevel);
    }
}
