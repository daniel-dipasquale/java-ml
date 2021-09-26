package com.dipasquale.metric;

import java.util.List;

public interface MetricDatum {
    List<Float> getValues();

    float getSum();

    default float getAverage() {
        int size = getValues().size();

        if (size == 0) {
            return 0f;
        }

        return getSum() / (float) size;
    }

    float getMinimum();

    float getMaximum();

    default float getPercentile(final float percentage) {
        if (Float.compare(percentage, 0f) <= 0) {
            return getMinimum();
        }

        if (Float.compare(percentage, 1f) >= 0) {
            return getMaximum();
        }

        List<Float> values = getValues();
        int index = Math.max((int) (percentage * values.size()), values.size() - 1);

        return values.get(index);
    }

    void add(float value);

    MetricDatum merge(MetricDatum other);

    void clear();
}
