package com.dipasquale.metric.concurrent;

import com.dipasquale.metric.LazyMetricDatum;
import com.dipasquale.metric.MetricDatum;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
public final class SynchronizedLazyMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 1171644070405351833L;
    private final MetricDatum metricDatum = new LazyMetricDatum();

    @Override
    public float getLastValue() {
        synchronized (metricDatum) {
            return metricDatum.getLastValue();
        }
    }

    @Override
    public float getSum() {
        synchronized (metricDatum) {
            return metricDatum.getSum();
        }
    }

    @Override
    public int getCount() {
        synchronized (metricDatum) {
            return metricDatum.getCount();
        }
    }

    @Override
    public float getAverage() {
        synchronized (metricDatum) {
            return metricDatum.getAverage();
        }
    }

    @Override
    public float getMinimum() {
        synchronized (metricDatum) {
            return metricDatum.getMinimum();
        }
    }

    @Override
    public float getMaximum() {
        synchronized (metricDatum) {
            return metricDatum.getMaximum();
        }
    }

    @Override
    public float getPercentile(final float percentage) {
        synchronized (metricDatum) {
            return metricDatum.getPercentile(percentage);
        }
    }

    @Override
    public void add(final float value) {
        synchronized (metricDatum) {
            metricDatum.add(value);
        }
    }

    @Override
    public void clear() {
        synchronized (metricDatum) {
            metricDatum.clear();
        }
    }
}
