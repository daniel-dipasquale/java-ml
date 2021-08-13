/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.deque;

import com.dipasquale.data.structure.collection.AbstractCollection;

import java.io.Serial;
import java.util.Deque;
import java.util.NoSuchElementException;

public abstract class AbstractDeque<T> extends AbstractCollection<T> implements Deque<T> {
    @Serial
    private static final long serialVersionUID = -1682404335072307069L;

    @Override
    public T peek() {
        return peekFirst();
    }

    @Override
    public T getFirst() {
        T item = peekFirst();

        if (item != null) {
            return item;
        }

        throw new NoSuchElementException("the deque is empty");
    }

    @Override
    public T getLast() {
        T item = peekLast();

        if (item != null) {
            return item;
        }

        throw new NoSuchElementException("the deque is empty");
    }

    @Override
    public T element() {
        return getFirst();
    }

    @Override
    public boolean offer(final T item) {
        return offerLast(item);
    }

    @Override
    public boolean add(final T item) {
        addLast(item);

        return true;
    }

    @Override
    public void push(final T item) {
        addFirst(item);
    }

    @Override
    public T remove() {
        return removeFirst();
    }

    @Override
    public T poll() {
        return removeFirst();
    }

    @Override
    public T pollFirst() {
        return removeFirst();
    }

    @Override
    public T pollLast() {
        return removeLast();
    }

    @Override
    public T pop() {
        return removeLast();
    }

    @Override
    public boolean removeFirstOccurrence(final Object item) {
        return remove(item);
    }

    @Override
    public boolean removeLastOccurrence(final Object item) {
        return remove(item);
    }
}
