package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = -2929938200172603143L;
    protected Float sum = null;
    protected Float minimum = null;
    protected Float maximum = null;
    protected Float lastValue = null;

    protected void initializeSummary(final float value) {
        sum = value;
        minimum = value;
        maximum = value;
        lastValue = value;
    }

    protected void amendSummary(final float value) {
        sum += value;
        minimum = Math.min(minimum, value);
        maximum = Math.max(maximum, value);
        lastValue = value;
    }

    protected void initializeSummary(final MetricDatum other) {
        sum = other.getSum();
        minimum = other.getMinimum();
        maximum = other.getMaximum();
        lastValue = other.getLastValue();
    }

    protected void mergeSummaries(final MetricDatum other) {
        sum += other.getSum();
        minimum = Math.min(minimum, other.getMinimum());
        maximum = Math.max(maximum, other.getMaximum());
        lastValue = other.getLastValue();
    }

    @Override
    public void clear() {
        sum = null;
        minimum = null;
        maximum = null;
        lastValue = null;
    }
}
