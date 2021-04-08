package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.collection.AbstractCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class Values<TKey, TValue> extends AbstractCollection<TValue> {
    @Serial
    private static final long serialVersionUID = 5859039696728285129L;
    private final AbstractMap<TKey, TValue> map;

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public final boolean contains(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public final boolean add(final TValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(final Object value) {
        Optional<TKey> keyToRemove = map.stream()
                .filter(e -> Objects.equals(e.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst();

        keyToRemove.ifPresent(map::remove);

        return keyToRemove.isPresent();
    }

    @Override
    public final boolean addAll(final Collection<? extends TValue> values) {
        throw new UnsupportedOperationException();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

    @Override
    public final boolean removeAll(final Collection<?> values) {
        Set<?> valuesToRemove = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = map.stream()
                .filter(e -> valuesToRemove.contains(e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public final boolean retainAll(final Collection<?> values) {
        Set<?> valuesToRetain = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = map.stream()
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
        return map.stream()
                .map(Map.Entry::getValue)
                .iterator();
    }
}
