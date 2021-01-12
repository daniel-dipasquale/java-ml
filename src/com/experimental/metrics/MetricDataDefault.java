package com.experimental.metrics;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class MetricDataDefault implements MetricData {
    @Getter
    private final long dateTime;
    private final Map<MetricKey, MetricDatum> metricsMap;
    @Getter
    private final Set<MetricKey> metricKeys;
    @Getter
    private final Set<Map.Entry<MetricKey, MetricDatum>> metrics;

    public MetricDataDefault(final long dateTime, final Map<MetricKey, MetricDatum> metricsMap) {
        this.dateTime = dateTime;
        this.metricsMap = metricsMap;
        this.metricKeys = Collections.unmodifiableSet(metricsMap.keySet());
        this.metrics = Collections.unmodifiableSet(metricsMap.entrySet());
    }

    @Override
    public MetricDatum getMetric(final MetricKey metricKey, final boolean avoidNull) {
        if (!avoidNull) {
            return metricsMap.get(metricKey);
        }

        return metricsMap.getOrDefault(metricKey, MetricDatumDefault.EMPTY);
    }
}
