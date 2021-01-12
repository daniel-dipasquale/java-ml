package com.experimental.metrics;

@FunctionalInterface
public interface MetricDataComparer {
    MetricDataComparison compare(Iterable<MetricData> metrics);
}
