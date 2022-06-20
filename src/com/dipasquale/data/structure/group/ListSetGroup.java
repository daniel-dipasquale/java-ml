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
public final class ListSetGroup<TKey extends Comparable<TKey>, TItem> implements Iterable<TItem>, Serializable {
    @Serial
    private static final long serialVersionUID = 2209041596810668817L;
    private final ItemKeyAccessor<TKey, TItem> itemKeyAccessor;
    @EqualsAndHashCode.Include
    private final List<Record<TKey, TItem>> list = new ArrayList<>();
    private final NavigableMap<TKey, TItem> navigableMap = new TreeMap<>(Comparator.naturalOrder());

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public TItem getByIndex(final int index) {
        return list.get(index).getValue();
    }

    public TItem getById(final TKey key) {
        return navigableMap.get(key);
    }

    public TItem getLast() {
        Map.Entry<TKey, TItem> entry = navigableMap.lastEntry();

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    public TItem set(final int index, final TItem item) {
        ArgumentValidatorSupport.ensureNotNull(item, "item");

        TKey key = itemKeyAccessor.getKey(item);
        Record<TKey, TItem> oldRecord = list.set(index, new Record<>(key, item));
        TKey oldKey = oldRecord.getKey();
        TItem oldItem = oldRecord.getValue();

        if (key != oldKey) {
            navigableMap.remove(oldKey);
        }

        if (key != oldKey || item != oldItem) {
            navigableMap.put(key, item);
        }

        if (item == oldItem) {
            return null;
        }

        return oldItem;
    }

    public void swap(final int fromIndex, final int toIndex) {
        Record<TKey, TItem> replacedRecord = list.set(toIndex, list.get(fromIndex));

        list.set(fromIndex, replacedRecord);
    }

    public TItem put(final TItem item) {
        ArgumentValidatorSupport.ensureNotNull(item, "item");

        TKey key = itemKeyAccessor.getKey(item);
        TItem oldItem = navigableMap.put(key, item);

        if (oldItem != null && oldItem != item) {
            list.removeIf(entry -> entry.getValue() == oldItem);
            list.add(new Record<>(key, item));
        } else if (oldItem == null) {
            list.add(new Record<>(key, item));
        }

        return oldItem;
    }

    public TItem removeByIndex(final int index) {
        Record<TKey, TItem> record = list.remove(index);

        return navigableMap.remove(record.getKey());
    }

    public TItem removeByKey(final TKey key) {
        TItem item = navigableMap.remove(key);

        if (item != null) {
            list.removeIf(entry -> entry.getValue() == item); // TODO: think of a better way of handling this
        }

        return item;
    }

    public Iterator<Pair<TItem>> fullJoin(final ListSetGroup<TKey, TItem> other) {
        return new ListSetGroupIterator<>(navigableMap, other.navigableMap);
    }

    @Override
    public Iterator<TItem> iterator() {
        return list.stream()
                .map(Record::getValue)
                .iterator();
    }

    public Iterator<TItem> sortedIterator() {
        return navigableMap.values().iterator();
    }
}
