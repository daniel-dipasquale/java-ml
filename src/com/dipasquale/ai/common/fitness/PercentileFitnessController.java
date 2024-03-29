package com.dipasquale.ai.common.fitness;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.StandardMetricDatumFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class PercentileFitnessController implements FitnessController, Serializable {
    @Serial
    private static final long serialVersionUID = -5316290173390134390L;
    private static final MetricDatumFactory METRIC_DATUM_FACTORY = StandardMetricDatumFactory.getInstance();
    private final MetricDatum metricDatum = METRIC_DATUM_FACTORY.create();
    private final float percentage;

    @Override
    public float get() {
        return metricDatum.getPercentile(percentage);
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
