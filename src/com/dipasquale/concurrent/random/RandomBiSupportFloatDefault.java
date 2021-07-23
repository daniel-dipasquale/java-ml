package com.dipasquale.concurrent.random;

import com.dipasquale.common.random.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomBiSupportFloatDefault implements RandomBiSupportFloat {
    @Serial
    private static final long serialVersionUID = -723845978593958930L;
    private final RandomSupportFloat randomSupport;
    private final RandomSupportFloat randomSupportContended;
    private final RandomBiSupportFloatContended contendedRandomBiSupport = new RandomBiSupportFloatContended();

    @Override
    public float next() {
        return randomSupport.next();
    }

    @Override
    public RandomBiSupportFloat selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return contendedRandomBiSupport;
    }

    private final class RandomBiSupportFloatContended implements RandomBiSupportFloat {
        @Serial
        private static final long serialVersionUID = 3779233376993807719L;

        @Override
        public float next() {
            return randomSupportContended.next();
        }

        @Override
        public RandomBiSupportFloat selectContended(final boolean contended) {
            return RandomBiSupportFloatDefault.this.selectContended(contended);
        }
    }
}
