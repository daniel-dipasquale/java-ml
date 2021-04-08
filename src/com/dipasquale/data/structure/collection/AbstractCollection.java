package com.dipasquale.data.structure.collection;

import com.dipasquale.data.structure.iterator.ZipIterator;
import com.google.common.collect.ImmutableList;

import java.io.Serial;
import java.io.Serializable;
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

public abstract class AbstractCollection<T> implements Collection<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -162665134155485514L;

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        int i = 0;

        for (T item : this) {
            array[i++] = item;
        }

        return array;
    }

    @Override
    public <R> R[] toArray(final R[] array) {
        int size = size();

        R[] arrayFixed = array.length < size
                ? (R[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        int i = 0;

        for (T item : this) {
            arrayFixed[i++] = (R) item;
        }

        return arrayFixed;
    }

    @Override
    public <R> R[] toArray(final IntFunction<R[]> generator) {
        return toArray(generator.apply(size()));
    }

    @Override
    public boolean containsAll(final Collection<?> items) {
        for (Object item : items) {
            if (!contains(item)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends T> items) {
        boolean modified = false;

        for (T item : items) {
            if (add(item)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(final Collection<?> items) {
        long removed = items.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        List<T> itemsToRemove = stream()
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
    public boolean retainAll(final Collection<?> items) {
        Set<?> itemsToRetain = ensureSet(items);

        if (!itemsToRetain.isEmpty()) {
            List<T> itemsToRemove = stream()
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

    private boolean equals(final Collection<T> other) {
        if (size() != other.size()) {
            return false;
        }

        List<Iterator<T>> iterators = ImmutableList.<Iterator<T>>builder()
                .add(iterator())
                .add(other.iterator())
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

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Collection) {
            try {
                return equals((Collection<T>) other);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        for (T item : this) {
            hashCode += item.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        Iterator<T> iterator = iterator();

        if (!iterator.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        int items = 0;

        sb.append('[');

        do {
            if (items++ > 0) {
                sb.append(',');
                sb.append(' ');
            }

            T item = iterator.next();

            sb.append(item == this ? "(this Collection)" : item);
        } while (iterator.hasNext());

        sb.append(']');

        return sb.toString();
    }
}
