package com.experimental.metrics;

public interface MetricDatumCollector {
    void add(double value);

    MetricDatum create();
}
