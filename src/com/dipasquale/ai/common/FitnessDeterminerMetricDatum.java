package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class FitnessDeterminerMetricDatum implements FitnessDeterminer {
    private final MetricDatum metricDatum = new MetricDatumSortPthBeforeRead(); // TODO: replace with MetricDatumReadWhileWriting
    private final Selector selector;

    @Override
    public float get() {
        return selector.getValue(metricDatum);
    }

    @Override
    public void add(final float fitness) {
        metricDatum.add(fitness);
    }

    @Override
    public void clear() {
        metricDatum.clear();
    }

    @FunctionalInterface
    public interface Selector {
        float getValue(MetricDatum metricDatum);
    }
}
