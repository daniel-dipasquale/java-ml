package com.dipasquale.ai.rl.neat.factory;

import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class RandomEnumFactory<T extends Enum<T>> implements EnumFactory<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -8894265990551272012L;
    private final RandomSupport randomSupport;
    private final List<? extends T> values;

    @Override
    public T create() {
        int index = randomSupport.nextInteger(0, values.size());

        return values.get(index);
    }
}
