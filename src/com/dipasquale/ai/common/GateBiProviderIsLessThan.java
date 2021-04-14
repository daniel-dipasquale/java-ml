package com.dipasquale.ai.common;

import com.dipasquale.concurrent.RandomBiSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

final class GateBiProviderIsLessThan implements GateBiProvider {
    @Serial
    private static final long serialVersionUID = -6273100292904208563L;
    private final RandomBiSupportFloat randomSupport;
    private final float rate;
    private final GateBiProviderIsLessThanContended contendedGateProvider;

    GateBiProviderIsLessThan(final RandomBiSupportFloat randomSupport, final float rate) {
        this.randomSupport = randomSupport;
        this.rate = rate;
        this.contendedGateProvider = new GateBiProviderIsLessThanContended();
    }

    private boolean isOn(final boolean contended) {
        return randomSupport.selectContended(contended).isLessThan(rate);
    }

    @Override
    public boolean isOn() {
        return isOn(false);
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

        @Override
        public boolean isOn() {
            return GateBiProviderIsLessThan.this.isOn(true);
        }

        @Override
        public GateBiProvider selectContended(final boolean contended) {
            return GateBiProviderIsLessThan.this.selectContended(contended);
        }
    }
}
