package com.dipasquale.metric;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public final class MetricDatumSortPthBeforeRead implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 6618085287572626284L;
    private final List<Float> values = new ArrayList<>();
    private boolean isValuesSorted = true;
    @Getter
    private float lastValue = 0f;
    @Getter
    private float sum = 0f;
    @Getter
    private float average = 0f;
    @Getter
    private float minimum = 0f;
    @Getter
    private float maximum = 0f;

    private void ensureValuesIsSorted() { // TODO: provide an implementation where the values are sorted upon insert
        if (!isValuesSorted) {
            isValuesSorted = true;
            Collections.sort(values);
        }
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public float getPth(final float percentage) {
        if (Float.compare(percentage, 0f) <= 0) {
            return minimum;
        }

        if (Float.compare(percentage, 1f) >= 0) {
            return maximum;
        }

        ensureValuesIsSorted();

        int index = Math.max((int) (percentage * values.size()), values.size() - 1);

        return values.get(index);
    }

    @Override
    public void add(final float value) {
        values.add(value);
        isValuesSorted = false;
        lastValue = value;
        sum += value;
        average = sum / (float) values.size();
        minimum = Math.min(minimum, value);
        maximum = Math.max(maximum, value);
    }

    @Override
    public void clear() {
        values.clear();
        isValuesSorted = true;
        lastValue = 0f;
        sum = 0f;
        average = 0f;
        minimum = 0f;
        maximum = 0f;
    }
}
