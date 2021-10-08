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

    @Override
    public void add(final float value) {
        int size = values.size();

        valuesSorted = size == 0;
        values.add(value);

        if (size == 0) {
            sum = value;
            minimum = value;
            maximum = value;
        } else {
            sum += value;
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
        }
    }

    @Override
    public void merge(final MetricDatum other) {
        if (other.getValues().isEmpty()) {
            return;
        }

        int size = values.size();

        valuesSorted = false;
        values.addAll(other.getValues());

        if (size == 0) {
            sum = other.getSum();
            minimum = other.getMinimum();
            maximum = other.getMaximum();
        } else {
            sum += other.getSum();
            minimum = Math.min(minimum, other.getMinimum());
            maximum = Math.max(maximum, other.getMaximum());
        }
    }

    @Override
    public MetricDatum createCopy() {
        LazyValuesMetricDatum metricDatum = new LazyValuesMetricDatum();

        metricDatum.valuesSorted = valuesSorted;
        metricDatum.values.addAll(values);
        metricDatum.sum = sum;
        metricDatum.minimum = minimum;
        metricDatum.maximum = maximum;

        return metricDatum;
    }

    @Override
    public MetricDatum createReduced() {
        LazyValuesMetricDatum metricDatum = new LazyValuesMetricDatum();

        if (!values.isEmpty()) {
            metricDatum.values.add(sum);
            metricDatum.sum = sum;
            metricDatum.minimum = sum;
            metricDatum.maximum = sum;
        }

        return metricDatum;
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
