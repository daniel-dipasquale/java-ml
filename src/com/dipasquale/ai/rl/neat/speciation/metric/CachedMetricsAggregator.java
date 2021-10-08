package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumSelector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CachedMetricsAggregator<T> implements MetricDatumSelector<T> {
    private final MetricDatumSelector<T> metricDatumAggregator;
    private T cachedMetrics = null;
    private MetricDatum aggregatedMetricDatum = null;

    @Override
    public MetricDatum selectMetricDatum(final T metrics) {
        if (cachedMetrics != metrics) {
            cachedMetrics = metrics;
            aggregatedMetricDatum = metricDatumAggregator.selectMetricDatum(metrics);
        }

        return aggregatedMetricDatum;
    }
}
