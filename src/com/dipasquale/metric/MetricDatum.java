package com.dipasquale.metric;

import java.util.List;

public interface MetricDatum {
    List<Float> getValues();

    Float getSum();

    default Float getAverage() {
        int size = getValues().size();

        if (size == 0) {
            return null;
        }

        return getSum() / (float) size;
    }

    Float getMinimum();

    Float getMaximum();

    default Float getPercentile(final float percentage) {
        if (Float.compare(percentage, 0f) <= 0) {
            return getMinimum();
        }

        if (Float.compare(percentage, 1f) >= 0) {
            return getMaximum();
        }

        List<Float> values = getValues();

        if (values.isEmpty()) {
            return null;
        }

        int index = Math.min((int) (percentage * values.size()), values.size() - 1);

        return values.get(index);
    }

    void add(float value);

    void merge(MetricDatum other);

    MetricDatum createCopy();

    MetricDatum createReduced();

    void clear();
}
