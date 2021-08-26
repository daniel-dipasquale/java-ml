package com.dipasquale.common.factory;

import java.util.Deque;

@FunctionalInterface
public interface DequeFactory {
    <T> Deque<T> create(Deque<T> other);
}
