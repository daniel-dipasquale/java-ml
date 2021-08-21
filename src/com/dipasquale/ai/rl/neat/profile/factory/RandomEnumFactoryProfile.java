package com.dipasquale.ai.rl.neat.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.random.float1.RandomSupport;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class RandomEnumFactoryProfile<T extends Enum<T>> extends AbstractObjectProfile<EnumFactory<T>> {
    @Serial
    private static final long serialVersionUID = -8894265990551272012L;

    public RandomEnumFactoryProfile(final boolean isOn, final RandomType type, final T[] values) {
        super(isOn, new DefaultEnumFactory<>(true, type, values), new DefaultEnumFactory<>(false, type, values));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultEnumFactory<T extends Enum<T>> implements EnumFactory<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 8456518950968902682L;
        private final RandomSupport randomSupport;
        private final List<? extends T> values;

        private DefaultEnumFactory(final boolean isOn, final RandomType type, final T[] values) {
            this.randomSupport = Constants.createRandomSupport(type, isOn);
            this.values = Lists.newArrayList(values);
        }

        @Override
        public T create() {
            int index = randomSupport.next(0, values.size());

            return values.get(index);
        }
    }
}
