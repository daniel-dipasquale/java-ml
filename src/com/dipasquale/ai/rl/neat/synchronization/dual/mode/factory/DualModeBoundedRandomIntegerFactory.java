package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DualModeBoundedRandomIntegerFactory implements IntegerFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 3322232093472816035L;
    private final DualModeRandomSupport randomSupport;
    private final int minimum;
    private final int maximum;

    @Override
    public int create() {
        return randomSupport.nextInteger(minimum, maximum);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        randomSupport.activateMode(concurrencyLevel);
    }
}
