package com.experimental.metrics;

public interface MetricDataComparison {
    Iterable<Long> getDateTimes();

    MetricDataComparisonResult getComparison(long dateTime, MetricKey metricKey, String statisticName);

    default MetricDataComparisonResult getComparison(final long dateTime, final MetricDataSelector metricDataSelector) {
        return getComparison(dateTime, metricDataSelector.getMetricKey(), metricDataSelector.getStatisticName());
    }
}
