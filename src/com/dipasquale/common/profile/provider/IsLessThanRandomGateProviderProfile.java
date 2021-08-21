package com.dipasquale.common.profile.provider;

import com.dipasquale.common.Pair;
import com.dipasquale.common.profile.AbstractObjectProfile;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class IsLessThanRandomGateProviderProfile extends AbstractObjectProfile<GateProvider> {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;

    public IsLessThanRandomGateProviderProfile(final boolean isOn, final Pair<RandomSupport> randomSupportPair, final float max) {
        super(isOn, new DefaultGateProvider(randomSupportPair.getLeft(), max), new DefaultGateProvider(randomSupportPair.getRight(), max));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultGateProvider implements GateProvider, Serializable {
        @Serial
        private static final long serialVersionUID = -8941152184671136191L;
        private final RandomSupport randomSupport;
        private final float max;

        @Override
        public boolean isOn() {
            return randomSupport.isLessThan(max);
        }
    }
}
