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
    private Float sum;
    @Getter
    private Float minimum;
    @Getter
    private Float maximum;

    LazyValuesMetricDatum() {
        List<Float> values = new ArrayList<>();

        this.valuesSorted = true;
        this.values = values;
        this.valuesReadOnly = Collections.unmodifiableList(values);
        this.sum = null;
        this.minimum = null;
        this.maximum = null;
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

    private static float ensureNotNull(final Float value) {
        if (value == null) {
            return 0f;
        }

        return value;
    }

    @Override
    public void add(final float value) {
        int size = values.size();

        valuesSorted = false;
        values.add(value);
        sum = ensureNotNull(sum) + value;

        if (size == 0) {
            minimum = value;
            maximum = value;
        } else {
            minimum = Math.min(ensureNotNull(minimum), value);
            maximum = Math.max(ensureNotNull(maximum), value);
        }
    }

    @Override
    public void merge(final MetricDatum other) {
        if (other.getValues().isEmpty()) {
            return;
        }

        valuesSorted = false;
        values.addAll(other.getValues());
        sum = ensureNotNull(sum) + other.getSum();
        minimum = Math.min(ensureNotNull(minimum), other.getMinimum());
        maximum = Math.max(ensureNotNull(maximum), other.getMaximum());
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
        sum = null;
        minimum = null;
        maximum = null;
    }
}
