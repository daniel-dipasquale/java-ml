package com.dipasquale.ai.rl.neat.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.random.float1.RandomSupport;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomFloatFactoryProfile extends AbstractObjectProfile<FloatFactory> {
    @Serial
    private static final long serialVersionUID = -791004180256061334L;

    public BoundedRandomFloatFactoryProfile(final boolean isOn, final RandomType type, final float min, final float max) {
        super(isOn, new DefaultFloatFactory(true, type, min, max), new DefaultFloatFactory(false, type, min, max));
    }

    private static final class DefaultFloatFactory implements FloatFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final RandomSupport randomSupport;
        private final float min;
        private final float max;

        private DefaultFloatFactory(final boolean isOn, final RandomType type, final float min, final float max) {
            this.randomSupport = Constants.createRandomSupport(type, isOn);
            this.min = min;
            this.max = max;
        }

        @Override
        public float create() {
            return randomSupport.next(min, max);
        }
    }
}
