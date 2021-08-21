package com.dipasquale.ai.rl.neat.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class RandomFloatFactoryProfile extends AbstractObjectProfile<FloatFactory> {
    @Serial
    private static final long serialVersionUID = 796774320554634960L;

    public RandomFloatFactoryProfile(final boolean isOn, final RandomType type) {
        super(isOn, new DefaultFloatFactory(true, type), new DefaultFloatFactory(false, type));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final RandomSupport randomSupport;

        private DefaultFloatFactory(final boolean isOn, final RandomType type) {
            this.randomSupport = Constants.createRandomSupport(type, isOn);
        }

        @Override
        public float create() {
            return randomSupport.next();
        }
    }
}
