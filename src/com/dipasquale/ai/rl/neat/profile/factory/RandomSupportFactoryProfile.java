package com.dipasquale.ai.rl.neat.profile.factory;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.random.float1.RandomSupport;

import java.io.Serial;

public final class RandomSupportFactoryProfile extends AbstractObjectProfile<RandomSupport> {
    @Serial
    private static final long serialVersionUID = -5824135271694994241L;

    public RandomSupportFactoryProfile(final boolean isOn, final RandomType type) {
        super(isOn, Constants.createRandomSupport(type, true), Constants.createRandomSupport(type, false));
    }
}
