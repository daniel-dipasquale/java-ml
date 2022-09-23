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
        T element = peekFirst();

        if (element != null) {
            return element;
        }

        throw new NoSuchElementException("the deque is empty");
    }

    @Override
    public T getLast() {
        T element = peekLast();

        if (element != null) {
            return element;
        }

        throw new NoSuchElementException("the deque is empty");
    }

    @Override
    public T element() {
        return getFirst();
    }

    @Override
    public boolean offer(final T element) {
        return offerLast(element);
    }

    @Override
    public boolean add(final T element) {
        addLast(element);

        return true;
    }

    @Override
    public void push(final T element) {
        addFirst(element);
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
    public boolean removeFirstOccurrence(final Object element) {
        return remove(element);
    }

    @Override
    public boolean removeLastOccurrence(final Object element) {
        return remove(element);
    }
}
