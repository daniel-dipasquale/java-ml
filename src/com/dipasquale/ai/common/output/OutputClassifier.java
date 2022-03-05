package com.dipasquale.ai.common.output;

import java.io.Serial;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public final class OutputClassifier<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2492693513349102434L;
    private final TreeMap<Float, T> ranges = new TreeMap<>();
    private float total = 0f;

    public void addRangeFor(final float range, final T type) {
        if (Float.compare(range, 0f) > 0) {
            ranges.put(total + range, type);
            total += range;
        }
    }

    public void addRemainingRangeFor(final T type) {
        if (Float.compare(total, 0f) > 0) {
            float totalFixed = (float) Math.ceil(total);

            if (Float.compare(totalFixed, total) > 0) {
                ranges.put(totalFixed, type);
                total = totalFixed;
            }
        } else {
            ranges.put(1f, type);
            total = 1f;
        }
    }

    public T classify(final float value) {
        try {
            return ranges.higherEntry(value).getValue();
        } catch (Exception e) {
            String message = String.format("%f is out of range", value);

            throw new NoSuchElementException(message);
        }
    }
}
