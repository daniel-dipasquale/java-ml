package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class EmptyMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 1989155526580797128L;
    private static final EmptyMetricDatum INSTANCE = new EmptyMetricDatum();
    private final List<Float> values = List.of();
    private final Float sum = null;
    private final Float minimum = null;
    private final Float maximum = null;
    private final Float lastValue = null;

    public static EmptyMetricDatum getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(final MetricDatum other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetricDatum createCopy() {
        return INSTANCE;
    }

    @Override
    public MetricDatum createReduced() {
        return INSTANCE;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
