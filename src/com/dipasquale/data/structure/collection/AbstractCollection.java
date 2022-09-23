package com.dipasquale.data.structure.collection;

import com.dipasquale.data.structure.iterable.IterableSupport;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public abstract class AbstractCollection<T> implements Collection<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -162665134155485514L;

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsAll(final Collection<?> elements) {
        for (Object element : elements) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends T> elements) {
        boolean modified = false;

        for (T element : elements) {
            if (add(element)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        List<T> elementsToRemove = stream()
                .filter(filter)
                .toList();

        elementsToRemove.forEach(this::remove);

        return !elementsToRemove.isEmpty();
    }

    @Override
    public boolean removeAll(final Collection<?> elements) {
        long removed = elements.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }

    private static Set<?> ensureSet(final Collection<?> elements) {
        Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());

        set.addAll(elements);

        return set;
    }

    @Override
    public boolean retainAll(final Collection<?> elements) {
        Set<?> elementsToRetain = ensureSet(elements);

        if (!elementsToRetain.isEmpty()) {
            List<T> elementsToRemove = stream()
                    .filter(key -> !elementsToRetain.contains(key))
                    .toList();

            if (elementsToRemove.isEmpty()) {
                return false;
            }

            elementsToRemove.forEach(this::remove);
        } else {
            clear();
        }

        return true;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        int i = 0;

        for (T element : this) {
            array[i++] = element;
        }

        return array;
    }

    @Override
    public <TArray> TArray[] toArray(final TArray[] array) {
        int size = size();

        TArray[] fixedArray = array.length < size
                ? (TArray[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        int i = 0;

        for (T element : this) {
            fixedArray[i++] = (TArray) element;
        }

        return fixedArray;
    }

    @Override
    public <TArray> TArray[] toArray(final IntFunction<TArray[]> generator) {
        return toArray(generator.apply(size()));
    }

    @Override
    public int hashCode() {
        return IterableSupport.hashCode(this);
    }

    private boolean equals(final Collection<T> other) {
        if (size() != other.size()) {
            return false;
        }

        return IterableSupport.equals(this, other);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Collection<?>) {
            try {
                return equals((Collection<T>) other);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return IterableSupport.toString(this);
    }
}
