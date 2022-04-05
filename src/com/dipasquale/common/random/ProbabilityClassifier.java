package com.dipasquale.common.random;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.TreeMap;

@RequiredArgsConstructor
public final class ProbabilityClassifier<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2492693513349102434L;
    private final TreeMap<Float, T> distribution = new TreeMap<>();
    private float totalWeight = 0f;

    public int size() {
        return distribution.size();
    }

    public boolean add(final float weight, final T value) {
        if (Float.compare(weight, 0f) <= 0) {
            return false;
        }

        float nextTotalWeight = totalWeight + weight;

        distribution.put(nextTotalWeight, value);
        totalWeight = nextTotalWeight;

        return true;
    }

    public T get(final float probability) {
        if (Float.compare(probability, 0f) < 0) {
            return null;
        }

        try {
            float index = probability * totalWeight;

            return distribution.higherEntry(index).getValue();
        } catch (Exception e) {
            return null;
        }
    }
}
