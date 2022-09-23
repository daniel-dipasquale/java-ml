package com.dipasquale.data.structure.iterator;

@FunctionalInterface
public interface NextElementProducer<T> {
    T next(T element);
}
