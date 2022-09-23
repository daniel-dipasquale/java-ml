package com.dipasquale.data.structure.group;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.Record;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ListSetGroup<TKey extends Comparable<TKey>, TElement> implements Iterable<TElement>, Serializable {
    @Serial
    private static final long serialVersionUID = 2209041596810668817L;
    private final ElementKeyAccessor<TKey, TElement> elementKeyAccessor;
    @EqualsAndHashCode.Include
    private final List<Record<TKey, TElement>> list = new ArrayList<>();
    private final NavigableMap<TKey, TElement> navigableMap = new TreeMap<>(Comparator.naturalOrder());

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public TElement getByIndex(final int index) {
        return list.get(index).getValue();
    }

    public TElement getById(final TKey key) {
        return navigableMap.get(key);
    }

    public TElement getLast() {
        Map.Entry<TKey, TElement> entry = navigableMap.lastEntry();

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    public TElement set(final int index, final TElement element) {
        ArgumentValidatorSupport.ensureNotNull(element, "element");

        TKey key = elementKeyAccessor.getKey(element);
        Record<TKey, TElement> oldRecord = list.set(index, new Record<>(key, element));
        TKey oldKey = oldRecord.getKey();
        TElement oldElement = oldRecord.getValue();

        if (key != oldKey) {
            navigableMap.remove(oldKey);
        }

        if (key != oldKey || element != oldElement) {
            navigableMap.put(key, element);
        }

        if (element == oldElement) {
            return null;
        }

        return oldElement;
    }

    public void swap(final int fromIndex, final int toIndex) {
        Record<TKey, TElement> replacedRecord = list.set(toIndex, list.get(fromIndex));

        list.set(fromIndex, replacedRecord);
    }

    public TElement put(final TElement element) {
        ArgumentValidatorSupport.ensureNotNull(element, "element");

        TKey key = elementKeyAccessor.getKey(element);
        TElement oldElement = navigableMap.put(key, element);

        if (oldElement != null && oldElement != element) {
            list.removeIf(entry -> entry.getValue() == oldElement);
            list.add(new Record<>(key, element));
        } else if (oldElement == null) {
            list.add(new Record<>(key, element));
        }

        return oldElement;
    }

    public TElement removeByIndex(final int index) {
        Record<TKey, TElement> record = list.remove(index);

        return navigableMap.remove(record.getKey());
    }

    public TElement removeByKey(final TKey key) {
        TElement element = navigableMap.remove(key);

        if (element != null) {
            list.removeIf(entry -> entry.getValue() == element); // TODO: think of a better way of handling this
        }

        return element;
    }

    public Iterator<Pair<TElement>> fullJoin(final ListSetGroup<TKey, TElement> other) {
        return new ListSetGroupIterator<>(navigableMap, other.navigableMap);
    }

    @Override
    public Iterator<TElement> iterator() {
        return list.stream()
                .map(Record::getValue)
                .iterator();
    }

    public Iterator<TElement> sortedIterator() {
        return navigableMap.values().iterator();
    }
}
