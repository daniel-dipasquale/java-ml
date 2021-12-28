package com.dipasquale.synchronization.dual.mode.gate;

import com.dipasquale.common.gate.Gate;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeIsLessThanRandomGate implements Gate, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;
    private final DualModeRandomSupport randomSupport;
    private final float max;

    @Override
    public boolean isOn() { // NOTE: benchmarking when most cases max are 0, since it should always be false
        return Float.compare(max, 0f) > 0 && randomSupport.isLessThan(max);
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
