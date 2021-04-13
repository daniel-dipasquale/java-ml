package com.dipasquale.common;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class IntegerFactoryCyclic implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = -8338656409981889475L;
    private final List<? extends IntegerFactory> factories;
    private int index;

    IntegerFactoryCyclic(final List<? extends IntegerFactory> factories, final int index) {
        this.factories = factories;
        this.index = index;
    }

    IntegerFactoryCyclic(final List<? extends IntegerFactory> factories) {
        this(factories, 0);
    }

    @Override
    public int create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }

    @Override
    public IntegerFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return new IntegerFactoryCyclicCas(factories, index);
    }

    private static final class IntegerFactoryCyclicCas implements IntegerFactory {
        @Serial
        private static final long serialVersionUID = -7862704243962425735L;
        private final List<? extends IntegerFactory> factories;
        private final AtomicInteger index;

        IntegerFactoryCyclicCas(final List<? extends IntegerFactory> factories, final int index) {
            this.factories = factories;
            this.index = new AtomicInteger(index);
        }

        @Override
        public int create() {
            int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

            return factories.get(indexFixed).create();
        }

        @Override
        public IntegerFactory selectContended(final boolean contended) {
            if (contended) {
                return this;
            }

            return new IntegerFactoryCyclic(factories, index.get());
        }
    }
}
