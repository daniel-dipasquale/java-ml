package com.dipasquale.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public final class OptimalPairSelector<TKey extends Comparable<TKey>, TValue> {
    private final Comparator<TKey> comparator;
    private TKey key = null;
    private TValue value = null;
    private int replacedCount;

    private void replace(final TKey newKey, final TValue newValue) {
        key = newKey;
        value = newValue;
        replacedCount++;
    }

    public boolean replaceValueIfBetter(final TKey candidateKey, final TValue candidateValue) {
        if (key != null && comparator.compare(candidateKey, key) <= 0) {
            return false;
        }

        replace(candidateKey, candidateValue);

        return true;
    }

    public boolean computeValueIfBetter(final TKey candidateKey, final Supplier<TValue> candidateValueSupplier) {
        if (key != null && comparator.compare(candidateKey, key) <= 0) {
            return false;
        }

        replace(candidateKey, candidateValueSupplier.get());

        return true;
    }
}
