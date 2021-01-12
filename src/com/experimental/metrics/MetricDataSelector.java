package com.experimental.metrics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class MetricDataSelector {
    private final MetricKey metricKey;
    private final String statisticName;
}
