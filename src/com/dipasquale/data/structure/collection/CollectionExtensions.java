package com.dipasquale.data.structure.collection;

import com.dipasquale.data.structure.iterator.ZipIterator;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionExtensions {
    public static <T> boolean isEmpty(final Collection<T> collection) {
        return collection.size() == 0;
    }

    public static <T> Object[] toArray(final Collection<T> collection) {
        Object[] array = new Object[collection.size()];
        int i = 0;

        for (T item : collection) {
            array[i++] = item;
        }

        return array;
    }

    public static <T, R> R[] toArray(final Collection<T> collection, final R[] array) {
        int size = collection.size();

        R[] arrayFixed = array.length < size
                ? (R[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        int i = 0;

        for (T item : collection) {
            arrayFixed[i++] = (R) item;
        }

        return arrayFixed;
    }

    public static <T, R> R[] toArray(final Collection<T> collection, final IntFunction<R[]> generator) {
        return toArray(collection, generator.apply(collection.size()));
    }

    public static <T> boolean containsAll(final Collection<T> collection, final Collection<?> items) {
        for (Object item : items) {
            if (!collection.contains(item)) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean addAll(final Collection<T> collection, final Collection<? extends T> items) {
        boolean modified = false;

        for (T item : items) {
            if (collection.add(item)) {
                modified = true;
            }
        }

        return modified;
    }

    public static <T> boolean removeAll(final Collection<T> collection, final Collection<?> items) {
        long removed = items.stream()
                .filter(collection::remove)
                .count();

        return removed > 0L;
    }

    public static <T> boolean removeIf(final Collection<T> collection, final Predicate<? super T> filter) {
        List<T> itemsToRemove = collection.stream()
                .filter(filter)
                .collect(Collectors.toList());

        itemsToRemove.forEach(collection::remove);

        return !itemsToRemove.isEmpty();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());

        set.addAll(collection);

        return set;
    }

    public static <T> boolean retainAll(final Collection<T> collection, final Collection<?> items) {
        Set<?> itemsToRetain = ensureSet(items);

        if (!itemsToRetain.isEmpty()) {
            List<T> itemsToRemove = collection.stream()
                    .filter(k -> !itemsToRetain.contains(k))
                    .collect(Collectors.toList());

            if (itemsToRemove.isEmpty()) {
                return false;
            }

            itemsToRemove.forEach(collection::remove);
        } else {
            collection.clear();
        }

        return true;
    }

    private static <T> boolean equals(final Collection<T> collection1, final Collection<T> collection2) {
        if (collection1.size() != collection2.size()) {
            return false;
        }

        List<Iterator<T>> iterators = ImmutableList.<Iterator<T>>builder()
                .add(collection1.iterator())
                .add(collection2.iterator())
                .build();

        ZipIterator<T> iterator = new ZipIterator<>(iterators);

        while (iterator.hasNext()) {
            List<T> items = iterator.next();

            if (!Objects.equals(items.get(0), items.get(1))) {
                return false;
            }
        }

        return true;
    }

    public static <T> boolean equals(final Collection<T> collection, final Object other) {
        if (collection == other) {
            return true;
        }

        if (other instanceof Collection) {
            try {
                return equals(collection, (Collection<T>) other);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    public static <T> int hashCode(final Collection<T> collection) {
        int hashCode = 0;

        for (T item : collection) {
            hashCode += item.hashCode();
        }

        return hashCode;
    }

    public static <T> String toString(final Collection<T> collection) {
        throw new NotImplementedException("fix this"); // TODO: fix this
    }
}
