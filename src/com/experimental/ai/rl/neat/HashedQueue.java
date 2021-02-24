package com.experimental.ai.rl.neat;

import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

final class HashedQueue<T> {
    private final Set<T> set = Sets.newSetFromMap(new LinkedHashMap<>());

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean push(final T value) {
        return set.add(value);
    }

    public T pop() {
        if (set.isEmpty()) {
            return null;
        }

        Iterator<T> iterator = set.iterator();

        try {
            return iterator.next();
        } finally {
            iterator.remove();
        }
    }
}
