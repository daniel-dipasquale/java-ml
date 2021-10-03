package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class DualModeRandomEnumFactory<T extends Enum<T>> implements EnumFactory<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8894265990551272012L;
    private final DualModeRandomSupport randomSupport;
    private final List<? extends T> values;

    @Override
    public T create() {
        int index = randomSupport.next(0, values.size());

        return values.get(index);
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
