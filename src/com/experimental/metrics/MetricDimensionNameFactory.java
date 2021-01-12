package com.experimental.metrics;

@FunctionalInterface
public interface MetricDimensionNameFactory {
    String create(MetricKey metricKey);
}
