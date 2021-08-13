/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.iterator;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class LinkedIterator<T> implements Iterator<T> {
    private final Navigator<T> nextItemNavigator;
    private final Predicate<T> hasNextItemPredicate;
    private Supplier<T> currentItemSupplier;

    private LinkedIterator(final T rootItem, final Navigator<T> nextItemNavigator, final Predicate<T> hasNextItemPredicate) {
        this.currentItemSupplier = () -> rootItem;
        this.nextItemNavigator = nextItemNavigator;
        this.hasNextItemPredicate = hasNextItemPredicate;
    }

    public static <T> Iterable<T> createIterable(final T root, final Navigator<T> nextItemGetter, final Predicate<T> hasNextItemPredicate) {
        return () -> new LinkedIterator<>(root, nextItemGetter, hasNextItemPredicate);
    }

    public static <T> Iterable<T> createIterable(final T root, final Navigator<T> nextItemGetter) {
        return createIterable(root, nextItemGetter, Objects::nonNull);
    }

    public static <T> Stream<T> createStream(final T root, final Navigator<T> nextItemGetter, final Predicate<T> hasNextItemPredicate) {
        return StreamSupport.stream(createIterable(root, nextItemGetter, hasNextItemPredicate).spliterator(), false);
    }

    public static <T> Stream<T> createStream(final T root, final Navigator<T> nextItemGetter) {
        return StreamSupport.stream(createIterable(root, nextItemGetter).spliterator(), false);
    }

    @Override
    public boolean hasNext() {
        return hasNextItemPredicate.hasMore(currentItemSupplier.get());
    }

    @Override
    public T next() {
        T currentItem = currentItemSupplier.get();

        currentItemSupplier = () -> nextItemNavigator.next(currentItem);

        return currentItem;
    }

    @FunctionalInterface
    public interface Navigator<T> {
        T next(T current);
    }

    @FunctionalInterface
    public interface Predicate<T> {
        boolean hasMore(T item);
    }
}
