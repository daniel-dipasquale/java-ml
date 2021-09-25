package com.dipasquale.ai.common.fitness;

import com.dipasquale.metric.LazyMetricDatum;
import com.dipasquale.metric.MetricDatum;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
final class MetricDatumFitnessDeterminer implements FitnessDeterminer, Serializable {
    @Serial
    private static final long serialVersionUID = -5316290173390134390L;
    private final MetricDatum metricDatum = new LazyMetricDatum(); // TODO: improve this if possible
    private final MetricDatumSelector metricDatumSelector;

    @Override
    public float get() {
        return metricDatumSelector.selectValue(metricDatum);
    }

    @Override
    public void add(final float fitness) {
        metricDatum.add(fitness);
    }

    @Override
    public void clear() {
        metricDatum.clear();
    }
}
