package com.experimental.ai.rl.neat;

import com.dipasquale.data.structure.set.InsertOrderSet;

final class HashedQueue<T> {
    private final InsertOrderSet<T> insertOrderSet = InsertOrderSet.create();

    public boolean isEmpty() {
        return insertOrderSet.isEmpty();
    }

    public boolean push(final T value) {
        return insertOrderSet.add(value);
    }

    public T pop() {
        if (insertOrderSet.isEmpty()) {
            return null;
        }

        return insertOrderSet.poll();
    }
}
