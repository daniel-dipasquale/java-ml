package com.dipasquale.metric;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class ZeroMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 1989155526580797128L;
    private static final ZeroMetricDatum INSTANCE = new ZeroMetricDatum();
    private final List<Float> values = ImmutableList.of();
    private final float sum = 0f;
    private final float minimum = 0f;
    private final float maximum = 0f;

    public static ZeroMetricDatum getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetricDatum merge(final MetricDatum other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
