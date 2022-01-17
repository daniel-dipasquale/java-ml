package com.dipasquale.synchronization.dual.mode.data.structure.set;

import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class DualModeSet<TItem, TSetFactory extends SetFactory & DualModeObject> implements Set<TItem>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -4728932779387492187L;
    private final TSetFactory setFactory;
    private Set<TItem> set;

    public DualModeSet(final TSetFactory setFactory) {
        this.setFactory = setFactory;
        this.set = setFactory.create(null);
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
    public boolean add(final TItem value) {
        return set.add(value);
    }

    @Override
    public boolean addAll(final Collection<? extends TItem> collection) {
        return set.addAll(collection);
    }

    @Override
    public boolean remove(final Object object) {
        return set.remove(object);
    }

    @Override
    public boolean removeIf(final Predicate<? super TItem> filter) {
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
    public Iterator<TItem> iterator() {
        return set.iterator();
    }

    @Override
    public void forEach(final Consumer<? super TItem> action) {
        set.forEach(action);
    }

    @Override
    public Spliterator<TItem> spliterator() {
        return set.spliterator();
    }

    @Override
    public Stream<TItem> stream() {
        return set.stream();
    }

    @Override
    public Stream<TItem> parallelStream() {
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
    public void activateMode(final int concurrencyLevel) {
        setFactory.activateMode(concurrencyLevel);
        set = setFactory.create(set);
    }
}
