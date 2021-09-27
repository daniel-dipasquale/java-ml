package com.dipasquale.metric;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class LazyValuesMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 6618085287572626284L;
    private boolean valuesSorted;
    private final List<Float> values;
    private final List<Float> valuesReadOnly;
    @Getter
    private float sum;
    @Getter
    private float minimum;
    @Getter
    private float maximum;

    LazyValuesMetricDatum() {
        List<Float> values = new ArrayList<>();

        this.valuesSorted = true;
        this.values = values;
        this.valuesReadOnly = Collections.unmodifiableList(values);
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
    public void merge(final MetricDatum other) {
        valuesSorted = false;
        values.addAll(other.getValues());
        sum += other.getSum();

        if (!other.getValues().isEmpty()) {
            minimum = Math.min(minimum, other.getMinimum());
            maximum = Math.max(maximum, other.getMaximum());
        }
    }

    @Override
    public MetricDatum createCopy() {
        LazyValuesMetricDatum copy = new LazyValuesMetricDatum();

        copy.valuesSorted = valuesSorted;
        copy.values.addAll(values);
        copy.sum = sum;
        copy.minimum = minimum;
        copy.maximum = maximum;

        return copy;
    }

    @Override
    public void clear() {
        valuesSorted = true;
        values.clear();
        sum = 0f;
        minimum = 0f;
        maximum = 0f;
    }
}
