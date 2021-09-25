package com.dipasquale.synchronization.dual.mode.data.structure.set;

import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class DualModeSet<T> implements Set<T>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -4800035066286223912L;
    private final SetFactoryProfile setFactoryProfile;
    private transient Set<T> set;

    private DualModeSet(final SetFactoryProfile setFactoryProfile, final Set<T> set) {
        this.setFactoryProfile = setFactoryProfile;
        this.set = setFactoryProfile.getObject().create(set);
    }

    private DualModeSet(final SetFactoryProfile setFactoryProfile) {
        this(setFactoryProfile, null);
    }

    public DualModeSet(final boolean concurrent, final SetFactory concurrentSetFactory, final SetFactory defaultSetFactory, final Set<T> set) {
        this(new SetFactoryProfile(concurrent, concurrentSetFactory, defaultSetFactory), set);
    }

    public DualModeSet(final boolean concurrent, final SetFactory concurrentSetFactory, final SetFactory defaultSetFactory) {
        this(concurrent, concurrentSetFactory, defaultSetFactory, null);
    }

    public DualModeSet(final boolean concurrent, final int numberOfThreads, final int initialCapacity, final Set<T> set) {
        this(SetFactoryProfile.create(concurrent, numberOfThreads, initialCapacity), set);
    }

    public DualModeSet(final boolean concurrent, final int numberOfThreads, final int initialCapacity) {
        this(concurrent, numberOfThreads, initialCapacity, null);
    }

    public DualModeSet(final boolean concurrent, final int numberOfThreads, final Set<T> set) {
        this(concurrent, numberOfThreads, 16, set);
    }

    public DualModeSet(final boolean concurrent, final int numberOfThreads) {
        this(concurrent, numberOfThreads, null);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(final Object object) {
        return set.contains(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean add(final T value) {
        return set.add(value);
    }

    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        return set.addAll(collection);
    }

    @Override
    public boolean remove(final Object object) {
        return set.remove(object);
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        return set.removeIf(filter);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return set.removeAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return set.retainAll(collection);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <TArray> TArray[] toArray(final TArray[] array) {
        return set.toArray(array);
    }

    @Override
    public <TArray> TArray[] toArray(final IntFunction<TArray[]> generator) {
        return set.toArray(generator);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        set.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return set.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return set.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return set.equals(obj);
    }

    @Override
    public String toString() {
        return set.toString();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        setFactoryProfile.switchProfile(concurrent);
        set = setFactoryProfile.getObject().create(set);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        set = setFactoryProfile.getObject().create((Set<T>) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(new HashSet<>(set));
    }

    private static <T> Set<T> createDefaultSet(final boolean parallel, final int numberOfThreads, final int initialCapacity, final Set<T> other) {
        if (parallel) {
            Set<T> set = Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity, 0.75f, numberOfThreads));

            if (other != null) {
                set.addAll(other);
            }

            return set;
        }

        if (other == null) {
            return new HashSet<>(initialCapacity);
        }

        return new HashSet<>(other);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxySetFactory implements SetFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 5483251986252952651L;
        private final boolean concurrent;
        private final int numberOfThreads;
        private final int initialCapacity;

        @Override
        public <T> Set<T> create(final Set<T> other) {
            return createDefaultSet(concurrent, numberOfThreads, initialCapacity, other);
        }
    }

    private static final class SetFactoryProfile extends AbstractObjectProfile<SetFactory> implements Serializable {
        @Serial
        private static final long serialVersionUID = 4399666040673908195L;

        private SetFactoryProfile(final boolean concurrent, final SetFactory concurrentSetFactory, final SetFactory defaultSetFactory) {
            super(concurrent, concurrentSetFactory, defaultSetFactory);
        }

        private static SetFactoryProfile create(final boolean concurrent, final int numberOfThreads, final int initialCapacity) {
            ProxySetFactory concurrentSetFactory = new ProxySetFactory(true, numberOfThreads, initialCapacity);
            ProxySetFactory defaultSetFactory = new ProxySetFactory(false, numberOfThreads, initialCapacity);

            return new SetFactoryProfile(concurrent, concurrentSetFactory, defaultSetFactory);
        }
    }
}
