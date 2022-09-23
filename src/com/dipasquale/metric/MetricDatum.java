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

    Float getLastValue();

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

        int size = values.size();
        int index = Math.min((int) (percentage * size), size - 1);

        return values.get(index);
    }

    void add(float value);

    default void add(final int value) {
        add((float) value);
    }

    default void add(final boolean value) {
        if (value) {
            add(1f);
        } else {
            add(0f);
        }
    }

    void merge(MetricDatum other);

    MetricDatum createCopy();

    MetricDatum createReduced();

    void clear();
}
