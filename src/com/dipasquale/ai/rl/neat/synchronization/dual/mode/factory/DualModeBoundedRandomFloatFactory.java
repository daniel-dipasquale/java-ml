package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeBoundedRandomFloatFactory implements FloatFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -791004180256061334L;
    private final DualModeRandomSupport randomSupport;
    private final float min;
    private final float max;

    @Override
    public float create() {
        return randomSupport.next(min, max);
    }

    @Override
    public int concurrencyLevel() {
        return randomSupport.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        randomSupport.activateMode(concurrencyLevel);
    }
}
