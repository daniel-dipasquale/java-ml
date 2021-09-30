package com.dipasquale.metric;

@FunctionalInterface
public interface MetricDatumValueSelector {
    Float selectValue(MetricDatum metricDatum);
}
