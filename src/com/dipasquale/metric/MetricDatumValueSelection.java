package com.dipasquale.metric;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetricDatumValueSelection {
    private static final Pattern STATISTIC_PATTERN = Pattern.compile("^(?<statistic>count|sum|avg|min|max|p)(?<percentage>\\d+(?:\\.\\d)?)?$");

    private static final Map<String, MetricDatumValueSelector> STATISTIC_SELECTORS = ImmutableMap.<String, MetricDatumValueSelector>builder()
            .put("count", md -> md.getValues().size())
            .put("sum", MetricDatum::getSum)
            .put("avg", MetricDatum::getAverage)
            .put("min", MetricDatum::getMinimum)
            .put("max", MetricDatum::getMaximum)
            .build();

    private final MetricDatum metricDatum;
    private final MetricDatumValueSelector metricDatumValueSelector;

    private static IllegalArgumentException createIllegalStatisticException(final String statistic) {
        String message = String.format("invalid statistic: %s", statistic);

        return new IllegalArgumentException(message);
    }

    public static MetricDatumValueSelection create(final MetricDatum metricDatum, final String statistic) {
        Matcher statisticMatcher = STATISTIC_PATTERN.matcher(statistic);

        if (!statisticMatcher.matches()) {
            throw createIllegalStatisticException(statistic);
        }

        MetricDatumValueSelector metricDatumValueSelector = STATISTIC_SELECTORS.get(statisticMatcher.group("statistic"));
        String percentage = statisticMatcher.group("percentage");

        if (metricDatumValueSelector != null && percentage != null) {
            throw createIllegalStatisticException(statistic);
        }

        if (metricDatumValueSelector != null) {
            return new MetricDatumValueSelection(metricDatum, metricDatumValueSelector);
        }

        float percentageFixed = Float.parseFloat(percentage);

        return new MetricDatumValueSelection(metricDatum, md -> md.getPercentile(percentageFixed));
    }

    public float getValue() {
        return metricDatumValueSelector.selectValue(metricDatum);
    }
}
