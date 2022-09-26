package com.dipasquale.data.structure.collection;

import com.dipasquale.common.factory.ObjectIndexProvider;
import com.dipasquale.data.structure.iterable.IterableSupport;
import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public final class IterableArray<T> implements Iterable<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -4987033620251239128L;
    private final LinkedElement root;
    private LinkedElement tail;
    private LinkedElement[] array;
    private int size;

    private IterableArray(final LinkedElement root, final int capacity) {
        this.root = root;
        this.tail = root;
        this.array = new LinkedElement[capacity];
        this.size = 0;
    }

    public IterableArray(final int capacity) {
        this(new LinkedElement(null, -1), capacity);
    }

    public int capacity() {
        return array.length;
    }

    public int size() {
        return size;
    }

    public T get(final int index) {
        return LinkedElement.extract(array[index]);
    }

    public T put(final int index, final T element) {
        LinkedElement linkedElement = new LinkedElement(element, index);
        LinkedElement oldLinkedElement = array[index];

        array[index] = linkedElement;

        if (oldLinkedElement == null) {
            tail.next = linkedElement;
            linkedElement.previous = tail;
            tail = linkedElement;
            size++;

            return null;
        }

        oldLinkedElement.previous.next = linkedElement;
        linkedElement.previous = oldLinkedElement.previous;

        if (oldLinkedElement.next != null) {
            oldLinkedElement.next.previous = linkedElement;
            linkedElement.next = oldLinkedElement.next;
        } else {
            tail = linkedElement;
        }

        return (T) oldLinkedElement.value;
    }

    public T compute(final int index, final Function<T, T> elementProvider) {
        LinkedElement oldLinkedElement = array[index];
        T oldElement = LinkedElement.extract(oldLinkedElement);
        T newElement = elementProvider.apply(oldElement);

        if (newElement != null) {
            if (newElement != oldElement) {
                put(index, newElement);
            }

            return newElement;
        }

        if (oldElement != null) {
            remove(index);
        }

        return null;
    }

    public T remove(final int index) {
        LinkedElement linkedElement = array[index];

        if (linkedElement == null) {
            return null;
        }

        array[index] = null;
        size--;

        linkedElement.previous.next = linkedElement.next;

        if (linkedElement.next != null) {
            linkedElement.next.previous = linkedElement.previous;
        } else {
            tail = tail.previous;
        }

        return (T) linkedElement.value;
    }

    public void clear() {
        for (LinkedElement linkedElement = root.next; linkedElement != null; linkedElement = linkedElement.next) {
            array[linkedElement.index] = null;
        }

        root.next = null;
        tail = root;
        size = 0;
    }

    public List<T> resize(final int capacity, final ObjectIndexProvider<T> elementIndexProvider) {
        if (array.length == capacity) {
            return List.of();
        }

        List<T> elementsRemoved = null;

        if (size > capacity) {
            elementsRemoved = new ArrayList<>();

            for (int i = size - 1; i >= capacity; i--) {
                T element = remove(i);

                if (element != null) {
                    elementsRemoved.add(element);
                }
            }
        }

        LinkedElement[] resizedArray = new LinkedElement[capacity];

        System.arraycopy(array, 0, resizedArray, 0, Math.min(array.length, capacity));
        array = resizedArray;

        if (size < capacity && elementIndexProvider != null) {
            for (int i = size; i < array.length; i++) {
                T element = elementIndexProvider.provide(i);

                if (element != null) {
                    put(i, element);
                }
            }
        }

        if (elementsRemoved == null || elementsRemoved.isEmpty()) {
            return List.of();
        }

        return Collections.unmodifiableList(elementsRemoved);
    }

    @Override
    public Iterator<T> iterator() {
        return LinkedIterator.createStream(root.next, linkedElement -> linkedElement.next)
                .map(LinkedElement::<T>extract)
                .iterator();
    }

    @Override
    public int hashCode() {
        return IterableSupport.hashCode(this);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof IterableArray<?>) {
            try {
                return IterableSupport.equals(this, (IterableArray<T>) other);
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class LinkedElement implements Serializable {
        @Serial
        private static final long serialVersionUID = 6842322620770776823L;
        private final Object value;
        private final int index;
        private LinkedElement previous = null;
        private LinkedElement next = null;

        private static <T> T extract(final LinkedElement linkedElement) {
            if (linkedElement == null) {
                return null;
            }

            return (T) linkedElement.value;
        }
    }
}
