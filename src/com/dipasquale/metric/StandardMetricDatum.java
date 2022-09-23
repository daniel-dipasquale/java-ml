package com.dipasquale.metric;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class StandardMetricDatum extends AbstractMetricDatum {
    @Serial
    private static final long serialVersionUID = 6618085287572626284L;
    private boolean sortedValues;
    private final List<Float> values;
    private final List<Float> readOnlyValues;

    private StandardMetricDatum(final List<Float> values) {
        this.sortedValues = true;
        this.values = values;
        this.readOnlyValues = Collections.unmodifiableList(values);
    }

    StandardMetricDatum() {
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
        boolean isEmpty = values.isEmpty();

        sortedValues = isEmpty;
        values.add(value);

        if (isEmpty) {
            initializeSummary(value);
        } else {
            amendSummary(value);
        }
    }

    @Override
    public void merge(final MetricDatum other) {
        if (other.getValues().isEmpty()) {
            return;
        }

        boolean isEmpty = values.isEmpty();

        sortedValues = false;
        values.addAll(other.getValues());

        if (isEmpty) {
            initializeSummary(other);
        } else {
            mergeSummaries(other);
        }
    }

    @Override
    public MetricDatum createCopy() {
        StandardMetricDatum metricDatum = new StandardMetricDatum();

        metricDatum.sortedValues = sortedValues;
        metricDatum.values.addAll(values);
        metricDatum.sum = sum;
        metricDatum.minimum = minimum;
        metricDatum.maximum = maximum;

        return metricDatum;
    }

    @Override
    public MetricDatum createReduced() {
        StandardMetricDatum metricDatum = new StandardMetricDatum();

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
