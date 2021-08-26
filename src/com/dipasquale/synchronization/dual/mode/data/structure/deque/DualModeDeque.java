package com.dipasquale.synchronization.dual.mode.data.structure.deque;

import com.dipasquale.common.factory.DequeFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeDeque<T> implements Deque<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 1118716298860387937L;
    private final DequeFactoryProfile dequeFactoryProfile;
    private transient Deque<T> deque;

    private DualModeDeque(final DequeFactoryProfile setFactoryProfile) {
        this(setFactoryProfile, setFactoryProfile.getObject().create(null));
    }

    public DualModeDeque(final boolean concurrent, final DequeFactory concurrentDequeFactory, final DequeFactory defaultDequeFactory) {
        this(new DequeFactoryProfile(concurrent, concurrentDequeFactory, defaultDequeFactory));
    }

    public DualModeDeque(final boolean concurrent) {
        this(DequeFactoryProfile.create(concurrent));
    }

    @Override
    public void addFirst(final T value) {
        deque.addFirst(value);
    }

    @Override
    public void addLast(final T value) {
        deque.addLast(value);
    }

    @Override
    public boolean offerFirst(final T value) {
        return deque.offerFirst(value);
    }

    @Override
    public boolean offerLast(final T value) {
        return deque.offerLast(value);
    }

    @Override
    public T removeFirst() {
        return deque.removeFirst();
    }

    @Override
    public T removeLast() {
        return deque.removeLast();
    }

    @Override
    public T pollFirst() {
        return deque.pollFirst();
    }

    @Override
    public T pollLast() {
        return deque.pollLast();
    }

    @Override
    public T getFirst() {
        return deque.getFirst();
    }

    @Override
    public T getLast() {
        return deque.getLast();
    }

    @Override
    public T peekFirst() {
        return deque.peekFirst();
    }

    @Override
    public T peekLast() {
        return deque.peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(final Object object) {
        return deque.removeFirstOccurrence(object);
    }

    @Override
    public boolean removeLastOccurrence(final Object object) {
        return deque.removeLastOccurrence(object);
    }

    @Override
    public boolean add(final T value) {
        return deque.add(value);
    }

    @Override
    public boolean offer(final T value) {
        return deque.offer(value);
    }

    @Override
    public T remove() {
        return deque.remove();
    }

    @Override
    public T poll() {
        return deque.poll();
    }

    @Override
    public T element() {
        return deque.element();
    }

    @Override
    public T peek() {
        return deque.peek();
    }

    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        return deque.addAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return deque.removeAll(collection);
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        return deque.removeIf(filter);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return deque.retainAll(collection);
    }

    @Override
    public void clear() {
        deque.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return deque.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return deque.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return deque.parallelStream();
    }

    @Override
    public void push(final T value) {
        deque.push(value);
    }

    @Override
    public T pop() {
        return deque.pop();
    }

    @Override
    public boolean remove(final Object object) {
        return deque.remove(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return deque.containsAll(collection);
    }

    @Override
    public boolean contains(final Object object) {
        return deque.contains(object);
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return deque.iterator();
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        deque.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return deque.toArray();
    }

    @Override
    public <TArray> TArray[] toArray(final TArray[] array) {
        return deque.toArray(array);
    }

    @Override
    public <TArray> TArray[] toArray(final IntFunction<TArray[]> generator) {
        return deque.toArray(generator);
    }

    @Override
    public Iterator<T> descendingIterator() {
        return deque.descendingIterator();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        dequeFactoryProfile.switchProfile(concurrent);
        deque = dequeFactoryProfile.getObject().create(deque);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        deque = dequeFactoryProfile.getObject().create((Deque<T>) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(new LinkedList<>(deque));
    }

    private static <T> Deque<T> createDefaultDeque(final boolean parallel, final Deque<T> other) {
        if (parallel && other != null) {
            return new ConcurrentLinkedDeque<>(other);
        }

        if (parallel) {
            return new ConcurrentLinkedDeque<>();
        }

        if (other != null) {
            return new LinkedList<>(other);
        }

        return new LinkedList<>();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxyDequeFactory implements DequeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 5483251986252952651L;
        private final boolean concurrent;

        @Override
        public <T> Deque<T> create(final Deque<T> other) {
            return createDefaultDeque(concurrent, other);
        }
    }

    private static final class DequeFactoryProfile extends AbstractObjectProfile<DequeFactory> implements Serializable {
        @Serial
        private static final long serialVersionUID = 4399666040673908195L;

        private DequeFactoryProfile(boolean isOn, final DequeFactory concurrentDequeFactory, final DequeFactory defaultDequeFactory) {
            super(isOn, concurrentDequeFactory, defaultDequeFactory);
        }

        private static DequeFactoryProfile create(final boolean concurrent) {
            ProxyDequeFactory concurrentDequeFactory = new ProxyDequeFactory(true);
            ProxyDequeFactory defaultDequeFactory = new ProxyDequeFactory(false);

            return new DequeFactoryProfile(concurrent, concurrentDequeFactory, defaultDequeFactory);
        }
    }
}
