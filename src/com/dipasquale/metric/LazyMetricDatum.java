package com.dipasquale.metric;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LazyMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 6618085287572626284L;
    private boolean valuesSorted;
    private final List<Float> values;
    private final List<Float> valuesReadOnly;
    @Getter
    private float lastValue;
    @Getter
    private float sum;
    @Getter
    private float minimum;
    @Getter
    private float maximum;

    public LazyMetricDatum() {
        List<Float> values = new ArrayList<>();

        this.valuesSorted = true;
        this.values = values;
        this.valuesReadOnly = Collections.unmodifiableList(values);
        this.lastValue = 0f;
        this.sum = 0f;
        this.minimum = 0f;
        this.maximum = 0f;
    }

    private List<Float> ensureValuesIsSorted() {
        if (!valuesSorted) {
            valuesSorted = true;
            Collections.sort(values);
        }

        return valuesReadOnly;
    }

    @Override
    public List<Float> getValues() {
        return ensureValuesIsSorted();
    }

    @Override
    public void add(final float value) {
        int size = values.size();

        valuesSorted = false;
        values.add(value);
        lastValue = value;
        sum += value;

        if (size == 0) {
            minimum = value;
            maximum = value;
        } else {
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
        }
    }

    @Override
    public MetricDatum merge(final MetricDatum other) {
        LazyMetricDatum merged = new LazyMetricDatum();

        merged.valuesSorted = false;
        merged.values.addAll(values);
        merged.values.addAll(other.getValues());

        if (other.getValues().isEmpty()) {
            merged.lastValue = lastValue;
        } else {
            merged.lastValue = other.getLastValue();
        }

        merged.sum = sum;
        merged.sum += other.getSum();

        if (other.getValues().isEmpty()) {
            merged.minimum = minimum;
            merged.maximum = maximum;
        } else {
            merged.minimum = Math.min(minimum, other.getMinimum());
            merged.maximum = Math.max(maximum, other.getMaximum());
        }

        return merged;
    }

    @Override
    public void clear() {
        valuesSorted = true;
        values.clear();
        lastValue = 0f;
        sum = 0f;
        minimum = 0f;
        maximum = 0f;
    }
}
