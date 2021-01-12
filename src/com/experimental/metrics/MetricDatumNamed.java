package com.experimental.metrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class MetricDatumNamed {
    private final String dimensionName;
    private final MetricDatum metricDatum;
}
