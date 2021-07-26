package com.experimental.metrics;

import com.dipasquale.common.factory.IdFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class MetricDatumCollectorConcurrent<T> implements MetricDatumCollector {
    private final IdFactory<T> idFactory;
    private final Map<T, MetricDatumCollectorDefault> collectors;

    public MetricDatumCollectorConcurrent(final IdFactory<T> idFactory, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this.idFactory = idFactory;
        this.collectors = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public void add(final double value) {
        T id = idFactory.createId();
        MetricDatumCollectorDefault metricDataCollector = collectors.computeIfAbsent(id, k -> new MetricDatumCollectorDefault());

        metricDataCollector.add(value);
    }

    @Override
    public MetricDatum create() {
        if (collectors.values().isEmpty()) {
            return MetricDatumDefault.EMPTY;
        }

        double sum = 0D;
        long count = 0L;
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;
        Queue<Queue<Double>> values = new LinkedList<>();

        for (MetricDatumCollectorDefault metricDataCollector : collectors.values()) {
            sum += metricDataCollector.getSum();
            count += metricDataCollector.getCount();
            minimum = Math.min(minimum, metricDataCollector.getMinimum());
            maximum = Math.max(maximum, metricDataCollector.getMaximum());
            values.add(metricDataCollector.getValues());
        }

        List<Double> pth = values.stream()
                .flatMap(Collection::stream)
                .sorted(Double::compareTo)
                .collect(Collectors.toList());

        return new MetricDatumDefault(sum, count, sum / count, minimum, maximum, pth);
    }
}
