package com.dipasquale.common.switcher;

import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class IsLessThanRandomGateProviderSwitcher extends AbstractObjectSwitcher<GateProvider> {
    @Serial
    private static final long serialVersionUID = -3883271729484274647L;
    private final Pair<RandomSupport> randomSupportPair;
    private final float max;
    @Getter(AccessLevel.PROTECTED)
    private final GateProvider on;
    @Getter(AccessLevel.PROTECTED)
    private final GateProvider off;

    public IsLessThanRandomGateProviderSwitcher(final boolean isOn, final Pair<RandomSupport> randomSupportPair, final float max) {
        super(isOn);
        this.randomSupportPair = randomSupportPair;
        this.max = max;
        this.on = new OnGateProvider();
        this.off = new OffGateProvider();
    }

    private boolean isLessThan(final RandomSupport randomSupport) {
        return randomSupport.isLessThan(max);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OnGateProvider implements GateProvider, Serializable {
        @Serial
        private static final long serialVersionUID = -8941152184671136191L;

        @Override
        public boolean isOn() {
            return isLessThan(randomSupportPair.getLeft());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OffGateProvider implements GateProvider, Serializable {
        @Serial
        private static final long serialVersionUID = -4302037639322147279L;

        @Override
        public boolean isOn() {
            return isLessThan(randomSupportPair.getRight());
        }
    }
}
