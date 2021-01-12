package com.experimental.metrics;

@FunctionalInterface
public interface MetricDatumRetriever {
    double getValue(MetricDatum metricDatum);
}
