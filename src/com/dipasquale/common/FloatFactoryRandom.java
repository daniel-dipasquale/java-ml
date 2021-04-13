package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

final class FloatFactoryRandom implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 3590158734451559869L;
    private final RandomSupportFloat randomSupport;
    private final float min;
    private final float max;
    private final RandomSupportFloat randomSupportContended;
    private final FloatFactoryRandomContended factoryContended;

    FloatFactoryRandom(final RandomSupportFloat randomSupport, final float min, final float max, final RandomSupportFloat randomSupportContended) {
        this.randomSupport = randomSupport;
        this.min = min;
        this.max = max;
        this.randomSupportContended = randomSupportContended;
        this.factoryContended = new FloatFactoryRandomContended();
    }

    @Override
    public float create() {
        return randomSupport.next(min, max);
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return factoryContended;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class FloatFactoryRandomContended implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 8943757011000247015L;

        @Override
        public float create() {
            return randomSupportContended.next(min, max);
        }

        @Override
        public FloatFactory selectContended(final boolean contended) {
            return FloatFactoryRandom.this.selectContended(contended);
        }
    }
}
