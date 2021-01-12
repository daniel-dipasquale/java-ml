package com.dipasquale.data.structure.map;

import lombok.RequiredArgsConstructor;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
final class Values<TKey, TValue> extends AbstractCollection<TValue> {
    private final Map<TKey, TValue> map;
    private final Iterable<Map.Entry<TKey, TValue>> iterable;

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

    private Stream<Map.Entry<TKey, TValue>> streamFromIterable() {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public final boolean contains(final Object value) {
        return streamFromIterable().anyMatch(e -> Objects.equals(e.getValue(), value));
    }

    @Override
    public final boolean add(final TValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean addAll(final Collection<? extends TValue> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(final Object value) {
        Optional<Map.Entry<TKey, TValue>> entryToRemove = streamFromIterable()
                .filter(e -> Objects.equals(e.getValue(), value))
                .findFirst();

        entryToRemove.map(Map.Entry::getKey).ifPresent(map::remove);

        return entryToRemove.isPresent();
    }

    @Override
    public final boolean removeAll(final Collection<?> values) {
        Set<?> valuesToRemove = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = streamFromIterable()
                .filter(e -> valuesToRemove.contains(e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public final boolean retainAll(final Collection<?> values) {
        Set<?> valuesToRetain = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = streamFromIterable()
                .filter(e -> !valuesToRetain.contains(e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public final void clear() {
        map.clear();
    }

    @Override
    public Iterator<TValue> iterator() {
        return streamFromIterable()
                .map(Map.Entry::getValue)
                .iterator();
    }
}
