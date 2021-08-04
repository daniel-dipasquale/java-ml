package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class RandomEnumFactorySwitcher<T extends Enum<T>> extends AbstractRandomFactorySwitcher<EnumFactory<T>> {
    @Serial
    private static final long serialVersionUID = -8894265990551272012L;
    private final List<? extends T> values;
    @Getter(AccessLevel.PROTECTED)
    private final EnumFactory<T> on;
    @Getter(AccessLevel.PROTECTED)
    private final EnumFactory<T> off;

    public RandomEnumFactorySwitcher(final boolean isOn, final RandomType type, final T[] values) {
        super(isOn, type);
        this.values = Lists.newArrayList(values);
        this.on = new DefaultEnumFactory(true);
        this.off = new DefaultEnumFactory(false);
    }

    private T create(final RandomSupport randomSupport) {
        int index = randomSupport.next(0, values.size());

        return values.get(index);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class DefaultEnumFactory implements EnumFactory<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 8456518950968902682L;
        private final boolean isOn;

        @Override
        public T create() {
            RandomSupport randomSupport = getRandomSupport(isOn);

            return RandomEnumFactorySwitcher.this.create(randomSupport);
        }
    }
}
