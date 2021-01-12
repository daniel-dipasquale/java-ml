package com.experimental.metrics;

import java.util.Map;
import java.util.Set;

public interface MetricData {
    long getDateTime();

    Set<MetricKey> getMetricKeys();

    Set<Map.Entry<MetricKey, MetricDatum>> getMetrics();

    MetricDatum getMetric(MetricKey metricKey, boolean avoidNull);

    default MetricDatum getMetric(final MetricKey metricKey) {
        return getMetric(metricKey, false);
    }
}
