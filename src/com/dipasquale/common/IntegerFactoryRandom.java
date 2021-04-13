package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

final class IntegerFactoryRandom implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -4001798150103502597L;
    private final RandomSupportFloat randomSupport;
    private final int min;
    private final int max;
    private final RandomSupportFloat randomSupportContended;
    private final IntegerFactoryRandomContended factoryContended;

    IntegerFactoryRandom(final RandomSupportFloat randomSupport, final int min, final int max, final RandomSupportFloat randomSupportContended) {
        this.randomSupport = randomSupport;
        this.min = min;
        this.max = max;
        this.randomSupportContended = randomSupportContended;
        this.factoryContended = new IntegerFactoryRandomContended();
    }

    @Override
    public int create() {
        return randomSupport.next(min, max);
    }

    @Override
    public IntegerFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return factoryContended;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class IntegerFactoryRandomContended implements IntegerFactory {
        @Serial
        private static final long serialVersionUID = 6304447461149982091L;

        @Override
        public int create() {
            return randomSupportContended.next(min, max);
        }

        @Override
        public IntegerFactory selectContended(final boolean contended) {
            return IntegerFactoryRandom.this.selectContended(contended);
        }
    }
}
