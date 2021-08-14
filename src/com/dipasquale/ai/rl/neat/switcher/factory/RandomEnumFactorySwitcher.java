package com.dipasquale.ai.rl.neat.switcher.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class RandomEnumFactorySwitcher<T extends Enum<T>> extends AbstractObjectSwitcher<EnumFactory<T>> {
    @Serial
    private static final long serialVersionUID = -8894265990551272012L;

    public RandomEnumFactorySwitcher(final boolean isOn, final RandomType type, final T[] values) {
        super(isOn, new DefaultEnumFactory<>(true, type, values), new DefaultEnumFactory<>(false, type, values));
    }

    private static <T> T create(final List<? extends T> values, final RandomSupport randomSupport) {
        int index = randomSupport.next(0, values.size());

        return values.get(index);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultEnumFactory<T extends Enum<T>> implements EnumFactory<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 8456518950968902682L;
        private final boolean isOn;
        private final RandomType type;
        private final List<? extends T> values;

        private DefaultEnumFactory(final boolean isOn, final RandomType type, final T[] values) {
            this.isOn = isOn;
            this.type = type;
            this.values = Lists.newArrayList(values);
        }

        @Override
        public T create() {
            RandomSupport randomSupport = Constants.getRandomSupport(type, isOn);

            return RandomEnumFactorySwitcher.create(values, randomSupport);
        }
    }
}
