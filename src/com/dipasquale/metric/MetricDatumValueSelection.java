package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetricDatumValueSelection {
    private static final Pattern STATISTIC_PATTERN = Pattern.compile("^(?<statistic>count|sum|avg|min|max|p)(?<percentage>\\d+(?:\\.\\d)?)?$");

    private static final Map<String, MetricDatumValueSelector> STATISTIC_SELECTORS = Map.ofEntries(
            Map.entry("count", metricDatum -> (float) metricDatum.getValues().size()),
            Map.entry("sum", MetricDatum::getSum),
            Map.entry("avg", MetricDatum::getAverage),
            Map.entry("min", MetricDatum::getMinimum),
            Map.entry("max", MetricDatum::getMaximum)
    );

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

        float percentageFixed = Float.parseFloat(String.format("0.%s", percentage));

        return new MetricDatumValueSelection(metricDatum, __metricDatum -> __metricDatum.getPercentile(percentageFixed));
    }

    public Float getValue() {
        return metricDatumValueSelector.selectValue(metricDatum);
    }
}
