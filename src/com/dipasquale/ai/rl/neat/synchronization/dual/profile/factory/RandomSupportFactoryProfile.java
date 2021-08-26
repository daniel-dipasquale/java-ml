package com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;

import java.io.Serial;

public final class RandomSupportFactoryProfile extends AbstractObjectProfile<RandomSupport> {
    @Serial
    private static final long serialVersionUID = -5824135271694994241L;

    public RandomSupportFactoryProfile(final boolean concurrent, final RandomType type) {
        super(concurrent, Constants.createRandomSupport(type, true), Constants.createRandomSupport(type, false));
    }
}
