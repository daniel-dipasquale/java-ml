package com.dipasquale.data.structure.collection;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface CollectionExtended<T> extends Collection<T> {
    @Override
    default boolean isEmpty() {
        return CollectionExtensions.isEmpty(this);
    }

    @Override
    default Object[] toArray() {
        return CollectionExtensions.toArray(this);
    }

    @Override
    default <R> R[] toArray(final R[] array) {
        return CollectionExtensions.toArray(this, array);
    }

    @Override
    default <R> R[] toArray(final IntFunction<R[]> generator) {
        return CollectionExtensions.toArray(this, generator);
    }

    @Override
    default boolean containsAll(final Collection<?> collection) {
        return CollectionExtensions.containsAll(this, collection);
    }

    @Override
    default boolean addAll(final Collection<? extends T> collection) {
        return CollectionExtensions.addAll(this, collection);
    }

    @Override
    default boolean removeAll(final Collection<?> collection) {
        return CollectionExtensions.removeAll(this, collection);
    }

    @Override
    default boolean removeIf(final Predicate<? super T> filter) {
        return CollectionExtensions.removeIf(this, filter);
    }

    @Override
    default boolean retainAll(final Collection<?> collection) {
        return CollectionExtensions.retainAll(this, collection);
    }
}
