package com.experimental.metrics;

@FunctionalInterface
public interface MetricGroupKeyFactory {
    MetricKey create(MetricKey metricKey);
}
