package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.collection.AbstractCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MapEntrySet<TKey, TValue> extends AbstractCollection<Map.Entry<TKey, TValue>> implements Set<Map.Entry<TKey, TValue>> {
    @Serial
    private static final long serialVersionUID = -7876365235786372188L;
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
    public boolean contains(final Object object) {
        if (object == null) {
            return false;
        }

        Map.Entry<TKey, TValue> entry = (Map.Entry<TKey, TValue>) object;
        TValue value = map.get(entry.getKey());

        return Objects.equals(entry.getValue(), value);
    }

    @Override
    public boolean add(final Map.Entry<TKey, TValue> entry) {
        return map.put(entry.getKey(), entry.getValue()) == null;
    }

    @Override
    public boolean remove(final Object object) {
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
    public boolean retainAll(final Collection<?> entries) {
        Map<TKey, TValue> entriesToRetain = entries.stream()
                .filter(entry -> entry instanceof Map.Entry<?, ?>)
                .map(entry -> (Map.Entry<TKey, TValue>) entry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map.Entry<TKey, TValue>> entriesToRemove = iteratorFactory.createStream()
                .filter(entry -> !entriesToRetain.containsKey(entry.getKey()) || !Objects.equals(entriesToRetain.get(entry.getKey()), entry.getValue()))
                .toList();

        entriesToRemove.forEach(entry -> map.remove(entry.getKey()));

        return !entriesToRemove.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<Map.Entry<TKey, TValue>> iterator() {
        return iteratorFactory.createIterator();
    }
}
