package com.experimental.metrics;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public final class MetricDatumCollectorDefault implements MetricDatumCollector {
    @Getter(AccessLevel.PACKAGE)
    private final Queue<Double> values = new LinkedList<>();
    @Getter(AccessLevel.PACKAGE)
    private double sum;
    @Getter(AccessLevel.PACKAGE)
    private long count;
    @Getter(AccessLevel.PACKAGE)
    private double minimum = Double.MAX_VALUE;
    @Getter(AccessLevel.PACKAGE)
    private double maximum = Double.MIN_VALUE;

    @Override
    public void add(final double value) {
        sum += value;
        count++;
        minimum = Math.min(minimum, value);
        maximum = Math.max(maximum, value);
        values.add(value);
    }

    @Override
    public MetricDatum create() {
        if (values.isEmpty()) {
            return MetricDatumDefault.EMPTY;
        }

        List<Double> valuesSorted = values.stream()
                .sorted(Double::compareTo)
                .collect(Collectors.toList());

        return new MetricDatumDefault(sum, count, sum / count, minimum, maximum, valuesSorted);
    }
}
