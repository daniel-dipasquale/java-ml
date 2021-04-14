package com.dipasquale.ai.common;

import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.concurrent.RandomBiSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

final class GateBiProviderIsLessThan implements GateBiProvider {
    @Serial
    private static final long serialVersionUID = -6273100292904208563L;
    private final RandomSupportFloat randomSupport;
    private final float rate;
    private final GateBiProviderIsLessThanContended contendedGateProvider;

    GateBiProviderIsLessThan(final RandomBiSupportFloat randomSupport, final float rate) {
        this.randomSupport = randomSupport.selectContended(false);
        this.rate = rate;
        this.contendedGateProvider = new GateBiProviderIsLessThanContended(randomSupport.selectContended(true));
    }

    @Override
    public boolean isOn() {
        return randomSupport.isLessThan(rate);
    }

    @Override
    public GateBiProvider selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedGateProvider;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private final class GateBiProviderIsLessThanContended implements GateBiProvider {
        @Serial
        private static final long serialVersionUID = -8007865741374430066L;
        private final RandomSupportFloat randomSupport;

        @Override
        public boolean isOn() {
            return randomSupport.isLessThan(rate);
        }

        @Override
        public GateBiProvider selectContended(final boolean contended) {
            return GateBiProviderIsLessThan.this.selectContended(contended);
        }
    }
}
