package com.dipasquale.data.structure.map;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class NavigableSubMap<TKey, TValue> implements NavigableMap<TKey, TValue> {
    private final NavigableMap<TKey, TValue> navigableMap;
    private final TKey fromKey;
    private final boolean fromInclusive;
    private final TKey toKey;
    private final boolean toInclusive;
    private final boolean ascending;
    private final SubIterable<TKey, TValue> iterable;
    private final KeySet<TKey, TValue> keySet;
    private final Values<TKey, TValue> values;
    private final EntrySet<TKey, TValue> entrySet;
    private final DescendingNavigableMap<TKey, TValue> descendingNavigableMap;
    private final NavigableKeySet<TKey, TValue> ascendingNavigableKeySet;
    private final NavigableKeySet<TKey, TValue> descendingNavigableKeySet;

    public NavigableSubMap(final NavigableMap<TKey, TValue> navigableMap, final TKey fromKey, final boolean fromInclusive, final TKey toKey, final boolean toInclusive, final boolean ascending, final SubIterable<TKey, TValue> iterable) {
        Iterable<Entry<TKey, TValue>> iterableDefault = () -> iterable.iterator(fromKey, fromInclusive, toKey, toInclusive, ascending);
        OrderedIterable<TKey, TValue> iterableOrdered = (asc) -> iterable.iterator(fromKey, fromInclusive, toKey, toInclusive, asc);
        DescendingNavigableMap<TKey, TValue> descendingNavigableMap = new DescendingNavigableMap<>(this, navigableMap.comparator(), iterable);

        this.navigableMap = navigableMap;
        this.fromKey = fromKey;
        this.fromInclusive = fromInclusive;
        this.toKey = toKey;
        this.toInclusive = toInclusive;
        this.ascending = ascending;
        this.iterable = iterable;
        this.keySet = new KeySet<>(navigableMap, iterableDefault);
        this.values = new Values<>(navigableMap, iterableDefault);
        this.entrySet = new EntrySet<>(navigableMap, iterableDefault);
        this.descendingNavigableMap = descendingNavigableMap;
        this.ascendingNavigableKeySet = new NavigableKeySet<>(navigableMap, iterableOrdered, ascending, iterable);
        this.descendingNavigableKeySet = new NavigableKeySet<>(descendingNavigableMap, iterableOrdered, !ascending, iterable);
    }

    private static <TKey, TValue> TKey getKey(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            return null;
        }

        return entry.getKey();
    }

    protected boolean isInRange(final TKey key) {
        int fromComparison = navigableMap.comparator().compare(key, fromKey);

        if (fromComparison < 0 || !fromInclusive && fromComparison == 0) {
            return false;
        }

        int toComparison = navigableMap.comparator().compare(key, toKey);

        return toComparison < 0 || toInclusive && toComparison == 0;
    }

    private Stream<Entry<TKey, TValue>> stream() {
        Iterable<Entry<TKey, TValue>> entries = () -> iterable.iterator(fromKey, fromInclusive, toKey, toInclusive, ascending);

        return StreamSupport.stream(entries.spliterator(), false);
    }

    @Override
    public int size() {
        return (int) stream().count();
    }

    @Override
    public boolean isEmpty() {
        return stream().findAny().isPresent();
    }

    @Override
    public boolean containsKey(final Object key) {
        return isInRange((TKey) key) && navigableMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return stream().anyMatch(e -> Objects.equals(e.getValue(), value) && isInRange(e.getKey()));
    }

    @Override
    public TValue get(final Object key) {
        if (!isInRange((TKey) key)) {
            return null;
        }

        return navigableMap.get(key);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        return navigableMap.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends TKey, ? extends TValue> map) {
        for (Map.Entry<? extends TKey, ? extends TValue> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public TValue remove(final Object key) {
        if (!isInRange((TKey) key)) {
            return null;
        }

        return navigableMap.remove(key);
    }

    @Override
    public void clear() {
        List<TKey> keys = stream()
                .map(Entry::getKey)
                .collect(Collectors.toList());

        keys.forEach(navigableMap::remove);
    }

    @Override
    public Set<TKey> keySet() {
        return keySet;
    }

    @Override
    public Collection<TValue> values() {
        return values;
    }

    @Override
    public Set<Entry<TKey, TValue>> entrySet() {
        return entrySet;
    }

    @Override
    public Comparator<? super TKey> comparator() {
        return navigableMap.comparator();
    }

    private Entry<TKey, TValue> ensureIsInRange(final Entry<TKey, TValue> entry) {
        if (entry == null || isInRange(entry.getKey())) {
            return null;
        }

        return entry;
    }

    @Override
    public Entry<TKey, TValue> firstEntry() {
        if (fromInclusive) {
            return ensureIsInRange(navigableMap.ceilingEntry(fromKey));
        }

        return ensureIsInRange(navigableMap.higherEntry(fromKey));
    }

    @Override
    public TKey firstKey() {
        return getKey(firstEntry());
    }

    @Override
    public Entry<TKey, TValue> floorEntry(final TKey key) {
        if (!isInRange(key)) {
            return null;
        }

        return ensureIsInRange(navigableMap.floorEntry(key));
    }

    @Override
    public TKey floorKey(final TKey key) {
        return getKey(floorEntry(key));
    }

    @Override
    public Entry<TKey, TValue> lowerEntry(final TKey key) {
        if (!isInRange(key)) {
            return null;
        }

        return ensureIsInRange(navigableMap.lowerEntry(key));
    }

    @Override
    public TKey lowerKey(final TKey key) {
        return getKey(lowerEntry(key));
    }

    @Override
    public Entry<TKey, TValue> higherEntry(final TKey key) {
        if (!isInRange(key)) {
            return null;
        }

        return ensureIsInRange(navigableMap.higherEntry(key));
    }

    @Override
    public TKey higherKey(final TKey key) {
        return getKey(higherEntry(key));
    }

    @Override
    public Entry<TKey, TValue> ceilingEntry(final TKey key) {
        if (!isInRange(key)) {
            return null;
        }

        return ensureIsInRange(navigableMap.ceilingEntry(key));
    }

    @Override
    public TKey ceilingKey(final TKey key) {
        return getKey(ceilingEntry(key));
    }

    @Override
    public Entry<TKey, TValue> lastEntry() {
        if (toInclusive) {
            return ensureIsInRange(navigableMap.floorEntry(toKey));
        }

        return ensureIsInRange(navigableMap.lowerEntry(toKey));
    }

    @Override
    public TKey lastKey() {
        return getKey(lastEntry());
    }

    @Override
    public Entry<TKey, TValue> pollFirstEntry() {
        Entry<TKey, TValue> entry = firstEntry();

        if (entry != null) {
            navigableMap.remove(entry.getKey());
        }

        return entry;
    }

    @Override
    public Entry<TKey, TValue> pollLastEntry() {
        Entry<TKey, TValue> entry = lastEntry();

        if (entry != null) {
            navigableMap.remove(entry.getKey());
        }

        return entry;
    }

    @Override
    public NavigableMap<TKey, TValue> descendingMap() {
        return descendingNavigableMap;
    }

    @Override
    public NavigableSet<TKey> navigableKeySet() {
        return ascendingNavigableKeySet;
    }

    @Override
    public NavigableSet<TKey> descendingKeySet() {
        return descendingNavigableKeySet;
    }

    @Override
    public NavigableMap<TKey, TValue> subMap(final TKey fromKey, final boolean fromInclusive, final TKey toKey, final boolean toInclusive) {
        return new NavigableSubMap<>(this, fromKey, fromInclusive, toKey, toInclusive, ascending, iterable);
    }

    @Override
    public SortedMap<TKey, TValue> subMap(final TKey fromKey, final TKey toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap<TKey, TValue> headMap(final TKey toKey, final boolean inclusive) {
        return subMap(firstKey(), true, toKey, inclusive);
    }

    @Override
    public SortedMap<TKey, TValue> headMap(final TKey toKey) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<TKey, TValue> tailMap(final TKey fromKey, final boolean inclusive) {
        return subMap(fromKey, inclusive, lastKey(), false);
    }

    @Override
    public SortedMap<TKey, TValue> tailMap(final TKey fromKey) {
        return tailMap(fromKey, true);
    }
}
