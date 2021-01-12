package com.experimental.metrics;

@FunctionalInterface
public interface MetricDatumRetrieverFactory {
    MetricDatumRetriever create(String statisticName);
}
