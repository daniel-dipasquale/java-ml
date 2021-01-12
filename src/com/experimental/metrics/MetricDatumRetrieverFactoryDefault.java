package com.experimental.metrics;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MetricDatumRetrieverFactoryDefault implements MetricDatumRetrieverFactory {
    private static final Pattern STATISTIC_PATTERN = Pattern.compile("^(?:(?<stat>(?i:sum|count|avg|average|min|minimum|max|maximum))|(?<statEx>(?i:p)(?<major>\\d+)(?:\\.(?<minor>\\d+))?))$");

    private static final Map<String, MetricDatumRetriever> METRIC_DATUM_RETRIEVERS = ImmutableMap.<String, MetricDatumRetriever>builder()
            .put("sum", MetricDatum::getSum)
            .put("count", MetricDatum::getCount)
            .put("avg", MetricDatum::getAverage)
            .put("average", MetricDatum::getAverage)
            .put("min", MetricDatum::getMinimum)
            .put("minimum", MetricDatum::getMinimum)
            .put("max", MetricDatum::getMaximum)
            .put("maximum", MetricDatum::getMaximum)
            .build();

    @Override
    public MetricDatumRetriever create(final String statisticName) {
        Matcher statisticMatcher = STATISTIC_PATTERN.matcher(statisticName);

        if (!statisticMatcher.find()) {
            throw new IllegalArgumentException("'statisticName' is incorrect");
        }

        String stat = statisticMatcher.group("stat");

        if (stat != null) {
            return METRIC_DATUM_RETRIEVERS.get(stat);
        }

        int major = Integer.parseInt(statisticMatcher.group("major"));
        int minor = Integer.parseInt(MoreObjects.firstNonNull(statisticMatcher.group("minor"), "0"));

        return md -> md.getPth(major, minor);
    }
}
