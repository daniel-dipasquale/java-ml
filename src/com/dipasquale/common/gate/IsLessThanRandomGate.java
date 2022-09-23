package com.dipasquale.common.gate;

import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class IsLessThanRandomGate implements Gate, Serializable {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;
    private final RandomSupport randomSupport;
    private final float maximum;

    @Override
    public boolean isOn() {
        return Float.compare(maximum, 0f) > 0 && randomSupport.isLessThan(maximum);
    }
}
