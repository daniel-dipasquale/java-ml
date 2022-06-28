package com.dipasquale.synchronization.dual.mode.gate;

import com.dipasquale.common.gate.Gate;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeIsLessThanRandomGate implements Gate, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;
    private final DualModeRandomSupport randomSupport;
    private final float maximum;

    @Override
    public boolean isOn() {
        return Float.compare(maximum, 0f) > 0 && randomSupport.isLessThan(maximum);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        randomSupport.activateMode(concurrencyLevel);
    }
}
