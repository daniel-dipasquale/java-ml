package com.dipasquale.concurrent;

import com.dipasquale.common.ArgumentValidator;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicLoopSelector<T> {
    private final ByIndex<T> items;
    private final AtomicInteger indexCas;
    private final int count;
    private final boolean restart;

    public AtomicLoopSelector(final ByIndex<T> items, final int offset, final int count, final boolean restart) {
        ArgumentValidator.ensureGreaterThanOrEqualToZero(offset, "offset");
        ArgumentValidator.ensureLessThan(offset, count, "offset");

        this.items = items;
        this.indexCas = new AtomicInteger(offset);
        this.count = count;
        this.restart = restart;
    }

    private int determineNextIndex(final int oldIndex, final int defaultIndex) {
        if (restart) {
            return (oldIndex + 1) % count;
        }

        if (oldIndex == defaultIndex) {
            return oldIndex;
        }

        int next = oldIndex + 1;

        if (next < count) {
            return next;
        }

        return defaultIndex;
    }

    private int nextIndex() {
        return indexCas.getAndAccumulate(-1, this::determineNextIndex);
    }

    public T next() {
        int index = nextIndex();

        if (index == -1) {
            return null;
        }

        return items.get(index);
    }

    @FunctionalInterface
    public interface ByIndex<T> {
        T get(int index);
    }
}