package com.dipasquale.concurrent;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class FloatBiFactoryCyclic implements FloatBiFactory {
    @Serial
    private static final long serialVersionUID = 868920995030701907L;
    private final List<? extends FloatBiFactory> factories;
    private int index;

    FloatBiFactoryCyclic(final List<? extends FloatBiFactory> factories, final int index) {
        this.factories = factories;
        this.index = index;
    }

    FloatBiFactoryCyclic(final List<? extends FloatBiFactory> factories) {
        this(factories, 0);
    }

    @Override
    public float create() {
        int indexOld = index;

        index = (index + 1) % factories.size();

        return factories.get(indexOld).create();
    }

    @Override
    public FloatBiFactory selectContended(final boolean contended) {
        if (!contended) {
            return this;
        }

        return new FloatBiFactoryCyclicCas(factories, index);
    }

    private static final class FloatBiFactoryCyclicCas implements FloatBiFactory {
        @Serial
        private static final long serialVersionUID = 8076896839207898329L;
        private final List<? extends FloatBiFactory> factories;
        private final AtomicInteger index;

        FloatBiFactoryCyclicCas(final List<? extends FloatBiFactory> factories, final int index) {
            this.factories = factories;
            this.index = new AtomicInteger(index);
        }

        @Override
        public float create() {
            int indexFixed = index.getAndAccumulate(-1, (oi, ni) -> (oi + 1) % factories.size());

            return factories.get(indexFixed).create();
        }

        @Override
        public FloatBiFactory selectContended(final boolean contended) {
            if (contended) {
                return this;
            }

            return new FloatBiFactoryCyclic(factories, index.get());
        }
    }
}
