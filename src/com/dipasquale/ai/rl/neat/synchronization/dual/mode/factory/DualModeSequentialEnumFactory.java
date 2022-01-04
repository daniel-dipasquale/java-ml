package com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory;

import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeCyclicIntegerValue;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class DualModeSequentialEnumFactory<T extends Enum<T>> implements EnumFactory<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3204668633787793221L;
    private final List<? extends T> values;
    private final DualModeCyclicIntegerValue index;

    public DualModeSequentialEnumFactory(final int concurrencyLevel, final List<? extends T> values) {
        this.values = values;
        this.index = new DualModeCyclicIntegerValue(concurrencyLevel, values.size(), -1);
    }

    @Override
    public T create() {
        return values.get(index.increment());
    }

    @Override
    public int concurrencyLevel() {
        return index.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        index.activateMode(concurrencyLevel);
    }
}
