package com.experimental.metrics;

import lombok.Getter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MetricDataComparisonDefault implements MetricDataComparison {
    private final Map<Long, Map<MetricKey, List<MetricDatumNamed>>> metrics;
    @Getter
    private final Set<Long> dateTimes;
    private final MetricDatumRetrieverFactory metricDatumRetrieverFactory;

    public MetricDataComparisonDefault(final Map<Long, Map<MetricKey, List<MetricDatumNamed>>> metrics, final MetricDatumRetrieverFactory metricDatumRetrieverFactory) {
        this.metrics = metrics;
        this.dateTimes = Collections.unmodifiableSet(metrics.keySet());
        this.metricDatumRetrieverFactory = metricDatumRetrieverFactory;
    }

    @Override
    public MetricDataComparisonResult getComparison(final long dateTime, final MetricKey metricKey, final String statisticName) {
        MetricDatumRetriever statisticValueRetriever = metricDatumRetrieverFactory.create(statisticName);
        Map<MetricKey, List<MetricDatumNamed>> metricGroups = metrics.get(dateTime);
        List<MetricDatumNamed> relevantMetrics = metricGroups.get(metricKey);

        if (relevantMetrics == null || relevantMetrics.isEmpty()) {
            return null;
        }

        List<MetricDatumNamed> relevantMetricsSorted = relevantMetrics.stream()
                .sorted(Comparator.comparingDouble(rm -> statisticValueRetriever.getValue(rm.getMetricDatum())))
                .collect(Collectors.toList());

        MetricDatumNamed relevantMetricBestPerformant = relevantMetricsSorted.get(0);
        double statisticValueBestPerformant = statisticValueRetriever.getValue(relevantMetricBestPerformant.getMetricDatum());

        return MetricDataComparisonResult.builder()
                .dateTime(dateTime)
                .metricKey(metricKey)
                .dimensionName(relevantMetricBestPerformant.getDimensionName())
                .statisticName(statisticName)
                .statisticValue(statisticValueRetriever.getValue(relevantMetricBestPerformant.getMetricDatum()))
                .degradedEntries(IntStream.range(1, relevantMetricsSorted.size())
                        .mapToObj(relevantMetricsSorted::get)
                        .map(rm -> new MetricDataComparisonResult.DegradedEntry(rm.getDimensionName(), statisticValueRetriever.getValue(rm.getMetricDatum()) / statisticValueBestPerformant))
                        .collect(Collectors.toList()))
                .build();
    }
}
