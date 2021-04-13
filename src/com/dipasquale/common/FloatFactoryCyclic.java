package com.dipasquale.common;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class FloatFactoryCyclic implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 868920995030701907L;
    private final List<? extends FloatFactory> factories;
    private int index;

    FloatFactoryCyclic(final List<? extends FloatFactory> factories, final int index) {
        this.factories = factories;
        this.index = index;
    }

    FloatFactoryCyclic(final List<? extends FloatFactory> factories) {
        this(factories, 0);
    }

    @Override
    public float create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return new FloatFactoryCyclicCas(factories, index);
    }

    private static final class FloatFactoryCyclicCas implements FloatFactory {
        @Serial
        private static final long serialVersionUID = 8076896839207898329L;
        private final List<? extends FloatFactory> factories;
        private final AtomicInteger index;

        FloatFactoryCyclicCas(final List<? extends FloatFactory> factories, final int index) {
            this.factories = factories;
            this.index = new AtomicInteger(index);
        }

        @Override
        public float create() {
            int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

            return factories.get(indexFixed).create();
        }

        @Override
        public FloatFactory selectContended(final boolean contended) {
            if (contended) {
                return this;
            }

            return new FloatFactoryCyclic(factories, index.get());
        }
    }
}
