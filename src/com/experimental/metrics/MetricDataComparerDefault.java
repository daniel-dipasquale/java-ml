package com.experimental.metrics;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class MetricDataComparerDefault implements MetricDataComparer {
    private final MetricGroupKeyFactory metricGroupKeyFactory;
    private final MetricDimensionNameFactory metricDimensionNameFactory;
    private final MetricDatumRetrieverFactory metricDatumRetrieverFactory;

    @Override
    public MetricDataComparison compare(final Iterable<MetricData> metrics) {
        Map<Long, Map<MetricKey, List<MetricDatumNamed>>> metricGroupsTimed = new HashMap<>();

        for (MetricData metric : metrics) {
            Map<MetricKey, List<MetricDatumNamed>> metricGroups = new HashMap<>();

            for (MetricKey metricKey : metric.getMetricKeys()) {
                MetricKey groupKey = metricGroupKeyFactory.create(metricKey);
                String dimensionName = metricDimensionNameFactory.create(metricKey);
                List<MetricDatumNamed> relevantMetrics = metricGroups.computeIfAbsent(groupKey, k -> new ArrayList<>());

                relevantMetrics.add(new MetricDatumNamed(dimensionName, metric.getMetric(metricKey)));
            }

            metricGroupsTimed.put(metric.getDateTime(), metricGroups);
        }

        return new MetricDataComparisonDefault(metricGroupsTimed, metricDatumRetrieverFactory);
    }
}
