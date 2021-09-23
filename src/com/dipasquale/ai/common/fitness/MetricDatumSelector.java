package com.dipasquale.ai.common.fitness;

import com.dipasquale.metric.MetricDatum;

@FunctionalInterface
public interface MetricDatumSelector {
    float selectValue(MetricDatum metricDatum);
}
