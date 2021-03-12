package com.dipasquale.data.structure.deque;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Deque;
import java.util.NoSuchElementException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DequeExtensions {
    public static <T> T peek(final Deque<T> deque) {
        return deque.peekFirst();
    }

    public static <T> T getFirst(final Deque<T> deque) {
        T item = deque.peekFirst();

        if (item == null) {
            throw new NoSuchElementException("the deque is empty");
        }

        return item;
    }

    public static <T> T getLast(final Deque<T> deque) {
        T item = deque.peekLast();

        if (item == null) {
            throw new NoSuchElementException("the deque is empty");
        }

        return item;
    }

    public static <T> T element(final Deque<T> deque) {
        return deque.getFirst();
    }

    public static <T> boolean offer(final Deque<T> deque, final T item) {
        return deque.offerLast(item);
    }

    public static <T> boolean add(final Deque<T> deque, final T item) {
        deque.addLast(item);

        return true;
    }

    public static <T> void push(final Deque<T> deque, final T item) {
        deque.addFirst(item);
    }

    public static <T> T remove(final Deque<T> deque) {
        return deque.removeFirst();
    }

    public static <T> T poll(final Deque<T> deque) {
        return deque.removeFirst();
    }

    public static <T> T pollFirst(final Deque<T> deque) {
        return deque.removeFirst();
    }

    public static <T> T pollLast(final Deque<T> deque) {
        return deque.removeLast();
    }

    public static <T> T pop(final Deque<T> deque) {
        return deque.removeLast();
    }

    public static <T> boolean removeFirstOccurrence(final Deque<T> deque, final Object item) {
        return deque.remove(item);
    }

    public static <T> boolean removeLastOccurrence(final Deque<T> deque, final Object item) {
        return deque.remove(item);
    }
}
