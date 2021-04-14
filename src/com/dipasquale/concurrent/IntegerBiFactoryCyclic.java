package com.dipasquale.concurrent;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class IntegerBiFactoryCyclic implements IntegerBiFactory {
    @Serial
    private static final long serialVersionUID = -8338656409981889475L;
    private final List<? extends IntegerBiFactory> factories;
    private int index;

    IntegerBiFactoryCyclic(final List<? extends IntegerBiFactory> factories, final int index) {
        this.factories = factories;
        this.index = index;
    }

    IntegerBiFactoryCyclic(final List<? extends IntegerBiFactory> factories) {
        this(factories, 0);
    }

    @Override
    public int create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }

    @Override
    public IntegerBiFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return new IntegerBiFactoryCyclicCas(factories, index);
    }

    private static final class IntegerBiFactoryCyclicCas implements IntegerBiFactory {
        @Serial
        private static final long serialVersionUID = -7862704243962425735L;
        private final List<? extends IntegerBiFactory> factories;
        private final AtomicInteger index;

        IntegerBiFactoryCyclicCas(final List<? extends IntegerBiFactory> factories, final int index) {
            this.factories = factories;
            this.index = new AtomicInteger(index);
        }

        @Override
        public int create() {
            int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

            return factories.get(indexFixed).create();
        }

        @Override
        public IntegerBiFactory selectContended(final boolean contended) {
            if (contended) {
                return this;
            }

            return new IntegerBiFactoryCyclic(factories, index.get());
        }
    }
}
