package com.experimental.metrics;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class MetricDatumDefault implements MetricDatum {
    public static final MetricDatumDefault EMPTY = new MetricDatumDefault(0D, 0L, 0D, 0D, 0D, ImmutableList.of());
    private final double sum;
    private final long count;
    private final double average;
    private final double minimum;
    private final double maximum;
    private final List<Double> values;

    @Override
    public double getPth(final int major, final int minor) {
        double percentage = ((double) major * 100D + (double) minor) / 10_000D;
        int index = (int) Math.floor((double) count * percentage);
        int indexFixed = Math.min(index, values.size() - 1);

        return values.get(indexFixed);
    }
}
