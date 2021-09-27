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
final class MapValues<TKey, TValue> extends AbstractCollection<TValue> {
    @Serial
    private static final long serialVersionUID = -5066011364774171880L;
    private final Map<TKey, TValue> map;
    private final IteratorFactory<TKey, TValue> iteratorFactory;

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean add(final TValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object value) {
        Optional<TKey> keyToRemove = iteratorFactory.createStream()
                .filter(e -> Objects.equals(e.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst();

        keyToRemove.ifPresent(map::remove);

        return keyToRemove.isPresent();
    }

    @Override
    public boolean addAll(final Collection<? extends TValue> values) {
        throw new UnsupportedOperationException();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> values) {
        Set<?> valuesToRemove = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = iteratorFactory.createStream()
                .filter(e -> valuesToRemove.contains(e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public boolean retainAll(final Collection<?> values) {
        Set<?> valuesToRetain = ensureSet(values);

        List<Map.Entry<TKey, TValue>> entriesToRemove = iteratorFactory.createStream()
                .filter(e -> !valuesToRetain.contains(e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<TValue> iterator() {
        return iteratorFactory.createStream()
                .map(Map.Entry::getValue)
                .iterator();
    }
}
