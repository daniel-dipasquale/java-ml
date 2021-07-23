package com.dipasquale.ai.common;

import com.dipasquale.concurrent.random.RandomBiSupportFloat;

public interface GateBiProvider extends GateProvider {
    GateBiProvider selectContended(boolean contended);

    static GateBiProvider createLiteral(final boolean value) {
        return new GateBiProviderLiteral(value);
    }

    static GateBiProvider createIsLessThan(final RandomBiSupportFloat randomSupport, final float rate) {
        return new GateBiProviderIsLessThan(randomSupport, rate);
    }
}
