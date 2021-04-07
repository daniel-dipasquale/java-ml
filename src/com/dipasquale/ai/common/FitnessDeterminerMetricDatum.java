package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
final class FitnessDeterminerMetricDatum implements FitnessDeterminer {
    @Serial
    private static final long serialVersionUID = -5316290173390134390L;
    private final MetricDatum metricDatum = new MetricDatumSortPthBeforeRead(); // TODO: replace with MetricDatumReadWhileWriting
    private final Selector selector;

    @Override
    public float get() {
        return selector.selectValue(metricDatum);
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
        float selectValue(MetricDatum metricDatum);
    }
}
