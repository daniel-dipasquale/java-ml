package com.experimental.metrics;

@FunctionalInterface
public interface MetricDatumCollectorFactory<T extends MetricDatumCollector> {
    T create();
}
