package com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;

import java.io.Serial;
import java.io.Serializable;

public final class BoundedRandomIntegerFactoryProfile extends AbstractObjectProfile<IntegerFactory> {
    @Serial
    private static final long serialVersionUID = 3322232093472816035L;

    public BoundedRandomIntegerFactoryProfile(final boolean concurrent, final RandomType type, final int min, final int max) {
        super(concurrent, new DefaultIntegerFactory(true, type, min, max), new DefaultIntegerFactory(false, type, min, max));
    }

    private static final class DefaultIntegerFactory implements IntegerFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -5038178366194681591L;
        private final RandomSupport randomSupport;
        private final int min;
        private final int max;

        private DefaultIntegerFactory(final boolean concurrent, final RandomType type, final int min, final int max) {
            this.randomSupport = Constants.createRandomSupport(type, concurrent);
            this.min = min;
            this.max = max;
        }

        @Override
        public int create() {
            return randomSupport.next(min, max);
        }
    }
}
