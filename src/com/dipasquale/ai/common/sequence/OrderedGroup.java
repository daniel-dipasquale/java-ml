package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
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

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class OrderedGroup<TId extends Comparable<TId>, TItem> implements Iterable<TItem>, Serializable {
    @Serial
    private static final long serialVersionUID = 2209041596810668817L;
    private static final String EMPTY = "";
    @EqualsAndHashCode.Include
    private final List<ItemIdEntry<TId, TItem>> list = new ArrayList<>();
    private final NavigableMap<TId, TItem> navigableMap = new TreeMap<>(Comparator.naturalOrder());

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public TItem getByIndex(final int index) {
        return list.get(index).item;
    }

    public TItem getById(final TId id) {
        return navigableMap.get(id);
    }

    public TItem getLast() {
        Map.Entry<TId, TItem> entry = navigableMap.lastEntry();

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    public TItem put(final TId id, final TItem item) {
        list.add(new ItemIdEntry<>(id, item));

        return navigableMap.put(id, item);
    }

    public TItem removeByIndex(final int index) {
        ItemIdEntry<TId, TItem> entry = list.remove(index);

        return navigableMap.remove(entry.id);
    }

    public TItem removeById(final TId id) {
        TItem item = navigableMap.remove(id);

        list.removeIf(e -> e.item == item);

        return item;
    }

    @Override
    public Iterator<TItem> iterator() {
        return list.stream()
                .map(e -> e.item)
                .iterator();
    }

    public Iterator<Pair<TItem>> fullJoin(final OrderedGroup<TId, TItem> other) {
        return new OrderedGroupIterator<>(navigableMap, other.navigableMap);
    }

    @Generated
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class ItemIdEntry<TId, TItem> implements Serializable {
        @Serial
        private static final long serialVersionUID = -274541428462625603L;
        private final TId id;
        private final TItem item;

        @Override
        public String toString() {
            if (item == null) {
                return EMPTY;
            }

            return item.toString();
        }
    }
}
