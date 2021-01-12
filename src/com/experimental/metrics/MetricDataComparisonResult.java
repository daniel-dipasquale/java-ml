package com.experimental.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@Getter
public final class MetricDataComparisonResult {
    private final long dateTime;
    private final MetricKey metricKey;
    private final String dimensionName;
    private final String statisticName;
    private final double statisticValue;
    private final List<DegradedEntry> degradedEntries;

    @RequiredArgsConstructor
    @Getter
    public static final class DegradedEntry {
        private final String dimensionName;
        private final double relativeRatio;
    }
}
