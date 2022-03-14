package com.dipasquale.metric;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public abstract class AbstractMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = -2929938200172603143L;
    protected Float sum = null;
    protected Float minimum = null;
    protected Float maximum = null;
    protected Float lastValue = null;

    protected void appendStatistics(final int size, final float value) {
        if (size == 0) {
            sum = value;
            minimum = value;
            maximum = value;
        } else {
            sum += value;
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
        }

        lastValue = value;
    }

    protected void mergeStatistics(final int size, final MetricDatum other) {
        if (size == 0) {
            sum = other.getSum();
            minimum = other.getMinimum();
            maximum = other.getMaximum();
            lastValue = other.getLastValue();
        } else if (!other.getValues().isEmpty()) {
            sum += other.getSum();
            minimum = Math.min(minimum, other.getMinimum());
            maximum = Math.max(maximum, other.getMaximum());
            lastValue = other.getLastValue();
        }
    }

    @Override
    public void clear() {
        sum = null;
        minimum = null;
        maximum = null;
        lastValue = null;
    }
}
