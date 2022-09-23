package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.CyclicIntegerValue;
import com.dipasquale.common.factory.EnumFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class SequentialEnumFactory<T extends Enum<T>> implements EnumFactory<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -3204668633787793221L;
    private final List<? extends T> values;
    private final CyclicIntegerValue index;

    public SequentialEnumFactory(final List<? extends T> values) {
        this.values = values;
        this.index = new CyclicIntegerValue(values.size(), -1);
    }

    @Override
    public T create() {
        return values.get(index.increment());
    }
}
