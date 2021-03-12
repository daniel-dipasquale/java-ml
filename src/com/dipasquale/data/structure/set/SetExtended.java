package com.dipasquale.data.structure.set;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface SetExtended<T> extends Set<T> {
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default Object[] toArray() {
        Object[] array = new Object[size()];
        int i = 0;

        for (T item : this) {
            array[i++] = item;
        }

        return array;
    }

    @Override
    default <R> R[] toArray(final R[] array) {
        R[] arrayFixed = array.length < size()
                ? (R[]) Array.newInstance(array.getClass().getComponentType(), size())
                : array;

        int i = 0;

        for (T item : this) {
            arrayFixed[i++] = (R) item;
        }

        return arrayFixed;
    }

    @Override
    default <R> R[] toArray(final IntFunction<R[]> generator) {
        return toArray(generator.apply(size()));
    }

    @Override
    default boolean containsAll(final Collection<?> collection) {
        for (Object item : collection) {
            if (!contains(item)) {
                return false;
            }
        }

        return true;
    }

    @Override
    default boolean addAll(final Collection<? extends T> collection) {
        boolean modified = false;

        for (T item : collection) {
            if (add(item)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    default boolean removeAll(final Collection<?> collection) {
        long removed = collection.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }

    @Override
    default boolean removeIf(final Predicate<? super T> filter) {
        List<T> itemsToRemove = StreamSupport.stream(spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());

        itemsToRemove.forEach(this::remove);

        return !itemsToRemove.isEmpty();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());

        set.addAll(collection);

        return set;
    }

    @Override
    default boolean retainAll(final Collection<?> collection) {
        Set<?> itemsToRetain = ensureSet(collection);

        if (!itemsToRetain.isEmpty()) {
            List<T> itemsToRemove = StreamSupport.stream(spliterator(), false)
                    .filter(k -> !itemsToRetain.contains(k))
                    .collect(Collectors.toList());

            if (itemsToRemove.isEmpty()) {
                return false;
            }

            itemsToRemove.forEach(this::remove);
        } else {
            clear();
        }

        return true;
    }
}
