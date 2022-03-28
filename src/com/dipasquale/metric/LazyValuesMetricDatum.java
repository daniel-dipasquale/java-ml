package com.dipasquale.metric;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class LazyValuesMetricDatum extends AbstractMetricDatum {
    @Serial
    private static final long serialVersionUID = 6618085287572626284L;
    private boolean sortedValues;
    private final List<Float> values;
    private final List<Float> readOnlyValues;

    private LazyValuesMetricDatum(final List<Float> values) {
        this.sortedValues = true;
        this.values = values;
        this.readOnlyValues = Collections.unmodifiableList(values);
    }

    LazyValuesMetricDatum() {
        this(new ArrayList<>());
    }

    private List<Float> ensureValuesIsSorted() {
        if (!sortedValues) {
            sortedValues = true;
            Collections.sort(values);
        }

        return readOnlyValues;
    }

    @Override
    public List<Float> getValues() {
        return ensureValuesIsSorted();
    }

    @Override
    public void add(final float value) {
        int size = values.size();

        sortedValues = size == 0;
        values.add(value);
        appendStatistics(size, value);
    }

    @Override
    public void merge(final MetricDatum other) {
        if (other.getValues().isEmpty()) {
            return;
        }

        int size = values.size();

        sortedValues = false;
        values.addAll(other.getValues());
        mergeStatistics(size, other);
    }

    @Override
    public MetricDatum createCopy() {
        LazyValuesMetricDatum metricDatum = new LazyValuesMetricDatum();

        metricDatum.sortedValues = sortedValues;
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
        sortedValues = true;
        values.clear();
        super.clear();
    }
}
