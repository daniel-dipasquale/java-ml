package com.dipasquale.common;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.function.Supplier;

@RequiredArgsConstructor
public final class EntryOptimizer<TKey extends Comparable<TKey>, TValue> {
    private final Comparator<TKey> comparator;
    private TKey optimumKey = null;
    private TValue optimumValue = null;

    public TKey getKey() {
        return optimumKey;
    }

    public TValue getValue() {
        return optimumValue;
    }

    public boolean collectIfMoreOptimum(final TKey key, final TValue value) {
        if (optimumKey != null && comparator.compare(key, optimumKey) <= 0) {
            return false;
        }

        optimumKey = key;
        optimumValue = value;

        return true;
    }

    public boolean computeIfMoreOptimum(final TKey key, final Supplier<TValue> supplier) {
        if (optimumKey != null && comparator.compare(key, optimumKey) <= 0) {
            return false;
        }

        optimumKey = key;
        optimumValue = supplier.get();

        return true;
    }
}
