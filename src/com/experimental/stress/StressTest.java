package com.experimental.stress;

import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataSelector;

public interface StressTest {
    String getName();

    void perform(MetricDataCollector metricDataCollector);

    Iterable<MetricDataSelector> extractMetricDataSelectors();

    Iterable<Throwable> extractExceptions();
}
