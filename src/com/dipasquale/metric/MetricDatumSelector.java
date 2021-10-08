package com.dipasquale.metric;

@FunctionalInterface
public interface MetricDatumSelector<T> {
    MetricDatum selectMetricDatum(T metrics);
}
