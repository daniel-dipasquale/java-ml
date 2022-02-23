package com.dipasquale.common;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.function.Supplier;

@RequiredArgsConstructor
public final class PairOptimizer<TKey extends Comparable<TKey>, TValue> {
    private final Comparator<TKey> comparator;
    private TKey optimalKey = null;
    private TValue optimalValue = null;

    public TKey getKey() {
        return optimalKey;
    }

    public TValue getValue() {
        return optimalValue;
    }

    public boolean replaceValueIfBetter(final TKey key, final TValue value) {
        if (optimalKey != null && comparator.compare(key, optimalKey) <= 0) {
            return false;
        }

        optimalKey = key;
        optimalValue = value;

        return true;
    }

    public boolean computeValueIfBetter(final TKey key, final Supplier<TValue> valueSupplier) {
        if (optimalKey != null && comparator.compare(key, optimalKey) <= 0) {
            return false;
        }

        optimalKey = key;
        optimalValue = valueSupplier.get();

        return true;
    }
}
