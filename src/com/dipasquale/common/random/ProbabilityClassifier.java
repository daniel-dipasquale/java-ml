package com.dipasquale.common.random;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.TreeMap;

@RequiredArgsConstructor
public final class ProbabilityClassifier<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2492693513349102434L;
    private static final float MAXIMUM_TOTAL_PROBABILITY = 1f;
    private final TreeMap<Float, T> probabilities = new TreeMap<>();
    private float totalProbability = 0f;

    public boolean addProbabilityFor(final float probability, final T value) {
        if (Float.compare(totalProbability, MAXIMUM_TOTAL_PROBABILITY) >= 0 || Float.compare(probability, 0f) <= 0) {
            return false;
        }

        float probabilityIndex = Math.min(totalProbability + probability, MAXIMUM_TOTAL_PROBABILITY);

        probabilities.put(probabilityIndex, value);
        totalProbability = probabilityIndex;

        return true;
    }

    public boolean addRemainingProbabilityFor(final T value) {
        if (Float.compare(totalProbability, MAXIMUM_TOTAL_PROBABILITY) >= 0) {
            return false;
        }

        probabilities.put(MAXIMUM_TOTAL_PROBABILITY, value);
        totalProbability = MAXIMUM_TOTAL_PROBABILITY;

        return true;
    }

    public T get(final float probability) {
        if (Float.compare(probability, 0f) < 0 || Float.compare(probability, MAXIMUM_TOTAL_PROBABILITY) >= 0) {
            return null;
        }

        return probabilities.higherEntry(probability).getValue();
    }
}
