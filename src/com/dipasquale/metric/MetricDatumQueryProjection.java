package com.dipasquale.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class MetricDatumQueryProjection {
    private final String id;
    private final String key;
    private final String statistic;
    private final String displayText;

    public MetricDatumQueryProjection(final String key, final String statistic, final String displayText) {
        this(displayText, key, statistic, displayText);
    }

    private static String createDisplayText(final String key, final String statistic) {
        return String.format("%s (%s)", key, statistic);
    }

    public MetricDatumQueryProjection(final String key, final String statistic) {
        this(key, statistic, createDisplayText(key, statistic));
    }

    @Override
    public String toString() {
        return getId();
    }
}
