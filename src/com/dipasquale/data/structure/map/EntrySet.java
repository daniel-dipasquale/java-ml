package com.dipasquale.data.structure.map;

import lombok.RequiredArgsConstructor;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
final class EntrySet<TKey, TValue> extends AbstractSet<Map.Entry<TKey, TValue>> {
    private final MapBase<TKey, TValue> map;

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public final boolean contains(final Object object) {
        if (object == null) {
            return false;
        }

        Map.Entry<TKey, TValue> entry = (Map.Entry<TKey, TValue>) object;
        TValue value = map.get(entry.getKey());

        return Objects.equals(entry.getValue(), value);
    }

    @Override
    public final boolean add(final Map.Entry<TKey, TValue> entry) {
        return map.put(entry.getKey(), entry.getValue()) == null;
    }

    @Override
    public final boolean remove(final Object object) {
        if (object == null) {
            return false;
        }

        Map.Entry<TKey, TValue> entry = (Map.Entry<TKey, TValue>) object;
        TValue value = map.get(entry.getKey());

        if (!Objects.equals(entry.getValue(), value)) {
            return false;
        }

        map.remove(entry.getKey());

        return true;
    }

    @Override
    public final boolean removeAll(final Collection<?> entries) {
        long removed = entries.stream()
                .filter(this::remove)
                .count();

        return removed > 0;
    }

    private Stream<Map.Entry<TKey, TValue>> streamFromMap() {
        Spliterator<Map.Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(map.iterator(), 0);

        return StreamSupport.stream(entries, false);
    }

    @Override
    public final boolean retainAll(final Collection<?> entries) {
        Map<TKey, TValue> entriesToRetain = entries.stream()
                .filter(e -> e instanceof Map.Entry)
                .map(e -> (Map.Entry<TKey, TValue>) e)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map.Entry<TKey, TValue>> entriesToRemove = streamFromMap()
                .filter(e -> !entriesToRetain.containsKey(e.getKey()) || !Objects.equals(entriesToRetain.get(e.getKey()), e.getValue()))
                .collect(Collectors.toList());

        entriesToRemove.forEach(e -> map.remove(e.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public final void clear() {
        map.clear();
    }

    @Override
    public Iterator<Map.Entry<TKey, TValue>> iterator() {
        return streamFromMap().iterator();
    }
}
