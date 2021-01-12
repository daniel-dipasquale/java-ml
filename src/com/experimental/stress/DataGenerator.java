package com.experimental.stress;

@FunctionalInterface
public interface DataGenerator<T> {
    Iterable<T> getAddGenerator();

    default Iterable<T> getRemoveGenerator() {
        return getAddGenerator();
    }

    default Iterable<T> getGetGenerator() {
        return getAddGenerator();
    }

    default Iterable<T> getIteratorGenerator() {
        return getAddGenerator();
    }
}
