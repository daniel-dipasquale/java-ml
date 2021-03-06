package com.dipasquale.ai.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public final class MetricDatumDefault implements MetricDatum {
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

    private void ensureValuesIsSorted() {
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
