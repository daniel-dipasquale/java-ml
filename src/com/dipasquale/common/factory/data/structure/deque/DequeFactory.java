package com.dipasquale.common.factory.data.structure.deque;

import java.util.Deque;

@FunctionalInterface
public interface DequeFactory {
    <T> Deque<T> create(Deque<T> other);

    default <T> Deque<T> create() {
        return create(null);
    }
}
