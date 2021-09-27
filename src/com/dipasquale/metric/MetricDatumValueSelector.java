package com.dipasquale.metric;

@FunctionalInterface
public interface MetricDatumValueSelector {
    float selectValue(MetricDatum metricDatum);
}
