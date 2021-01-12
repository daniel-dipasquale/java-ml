package com.experimental.metrics;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.IdFactory;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MetricDataCollectorConcurrent<T> implements MetricDataCollector {
    private final DateTimeSupport dateTimeSupport;
    private final long time;
    private final IdFactory<T> idFactory;
    private final int initialCapacity;
    private final float loadFactor;
    private final int concurrencyLevel;
    private final Map<Long, Map<String, MetricDatumCollectorConcurrent<T>>> timeFramedMetrics;
    private final Map<Long, Map<String, Unit<?>>> timeFramedUnits;
    private long lastTimeFrameFlushed;

    public MetricDataCollectorConcurrent(final DateTimeSupport dateTimeSupport, final long time, final Unit<Duration> unit, final IdFactory<T> idFactory, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        long timeInExpectedUnit = (long) unit.getConverterTo(dateTimeSupport.unit()).convert((double) time);

        this.dateTimeSupport = dateTimeSupport;
        this.time = timeInExpectedUnit;
        this.idFactory = idFactory;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.concurrencyLevel = concurrencyLevel;
        this.timeFramedMetrics = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
        this.timeFramedUnits = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
        this.lastTimeFrameFlushed = dateTimeSupport.getCurrentTimeFrame(timeInExpectedUnit) - timeInExpectedUnit;
    }

    @Override
    public void add(final String name, final double value, final Unit<?> unit) {
        long timeFrame = dateTimeSupport.getCurrentTimeFrame(time);
        Map<String, MetricDatumCollectorConcurrent<T>> metrics = timeFramedMetrics.computeIfAbsent(timeFrame, tf -> new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel));
        Map<String, Unit<?>> units = timeFramedUnits.computeIfAbsent(timeFrame, tf -> new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel));
        MetricDatumCollectorConcurrent<T> collector = metrics.computeIfAbsent(name, n -> new MetricDatumCollectorConcurrent<>(idFactory, initialCapacity, loadFactor, concurrencyLevel));

        collector.add(value);
        units.put(name, unit);
    }

    private Map<MetricKey, MetricDatum> createMetrics(final Map<String, MetricDatumCollectorConcurrent<T>> metrics, final Map<String, Unit<?>> units) {
        Map<MetricKey, MetricDatum> output = new HashMap<>();

        for (Map.Entry<String, MetricDatumCollectorConcurrent<T>> entry : metrics.entrySet()) {
            String name = entry.getKey();
            Unit<?> unit = units.get(name);
            MetricKeyDefault key = new MetricKeyDefault(name, unit);
            MetricDatum datum = entry.getValue().create();

            output.put(key, datum);
        }

        return output;
    }

    @Override
    public synchronized List<MetricData> flush(final boolean force) {
        List<MetricData> output = new ArrayList<>();
        long currentTimeFrame = dateTimeSupport.getCurrentTimeFrame(time);
        long nextTimeFrameToFlush = lastTimeFrameFlushed + time;

        while (nextTimeFrameToFlush < currentTimeFrame || force && nextTimeFrameToFlush == currentTimeFrame) {
            Map<String, MetricDatumCollectorConcurrent<T>> metrics = timeFramedMetrics.remove(nextTimeFrameToFlush);

            if (metrics != null) {
                Map<String, Unit<?>> units = timeFramedUnits.remove(nextTimeFrameToFlush);

                output.add(new MetricDataDefault(nextTimeFrameToFlush, createMetrics(metrics, units)));
            } else {
                output.add(new MetricDataDefault(nextTimeFrameToFlush, new HashMap<>()));
            }

            nextTimeFrameToFlush += time;
        }

        lastTimeFrameFlushed = nextTimeFrameToFlush - time;

        return output;
    }
}
