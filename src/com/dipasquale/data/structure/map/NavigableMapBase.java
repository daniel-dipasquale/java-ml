package com.dipasquale.data.structure.map;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;

public abstract class NavigableMapBase<TKey, TValue> extends MapBase<TKey, TValue> implements NavigableMap<TKey, TValue> {
    protected final Comparator<? super TKey> comparator;
    private final DescendingNavigableMap<TKey, TValue> descendingNavigableMap;
    private final NavigableKeySet<TKey, TValue> ascendingNavigableKeySet;
    private final NavigableKeySet<TKey, TValue> descendingNavigableKeySet;

    protected NavigableMapBase(final Comparator<? super TKey> comparator) {
        DescendingNavigableMap<TKey, TValue> descendingNavigableMap = new DescendingNavigableMap<>(this, comparator, this::iterator);

        this.comparator = comparator;
        this.descendingNavigableMap = descendingNavigableMap;
        this.ascendingNavigableKeySet = new NavigableKeySet<>(this, this::iterator, true, this::iterator);
        this.descendingNavigableKeySet = new NavigableKeySet<>(descendingNavigableMap, this::iterator, false, this::iterator);
    }

    private static <TKey, TValue> TKey getKey(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            return null;
        }

        return entry.getKey();
    }

    @Override
    public Comparator<? super TKey> comparator() {
        return comparator;
    }

    @Override
    public abstract Entry<TKey, TValue> firstEntry();

    @Override
    public TKey firstKey() {
        return getKey(firstEntry());
    }

    @Override
    public abstract Entry<TKey, TValue> floorEntry(TKey key);

    @Override
    public TKey floorKey(final TKey key) {
        return getKey(floorEntry(key));
    }

    @Override
    public abstract Entry<TKey, TValue> lowerEntry(TKey key);

    @Override
    public TKey lowerKey(final TKey key) {
        return getKey(lowerEntry(key));
    }

    @Override
    public abstract Entry<TKey, TValue> higherEntry(TKey key);

    @Override
    public TKey higherKey(final TKey key) {
        return getKey(higherEntry(key));
    }

    @Override
    public abstract Entry<TKey, TValue> ceilingEntry(TKey key);

    @Override
    public TKey ceilingKey(final TKey key) {
        return getKey(ceilingEntry(key));
    }

    @Override
    public abstract Entry<TKey, TValue> lastEntry();

    @Override
    public TKey lastKey() {
        return getKey(lastEntry());
    }

    @Override
    public Entry<TKey, TValue> pollFirstEntry() {
        Entry<TKey, TValue> entry = firstEntry();

        if (entry != null) {
            remove(entry.getKey());
        }

        return entry;
    }

    @Override
    public Entry<TKey, TValue> pollLastEntry() {
        Entry<TKey, TValue> entry = lastEntry();

        if (entry != null) {
            remove(entry.getKey());
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
        return new NavigableSubMap<>(this, fromKey, fromInclusive, toKey, toInclusive, true, this::iterator);
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

    protected abstract Iterator<Entry<TKey, TValue>> iterator(TKey fromKey, boolean fromInclusive, TKey toKey, boolean toInclusive, boolean ascending);

    protected Iterator<Entry<TKey, TValue>> iterator(final TKey fromKey, final TKey toKey, final boolean ascending) {
        return iterator(fromKey, true, toKey, false, ascending);
    }

    protected Iterator<Entry<TKey, TValue>> iteratorFrom(final TKey key, final boolean ascending) {
        return iterator(key, lastKey(), ascending);
    }

    protected Iterator<Entry<TKey, TValue>> iteratorTo(final TKey key, final boolean ascending) {
        return iterator(firstKey(), key, ascending);
    }

    protected Iterator<Entry<TKey, TValue>> iterator(final boolean ascending) {
        return iterator(firstKey(), lastKey(), ascending);
    }

    @Override
    protected Iterator<Entry<TKey, TValue>> iterator() {
        return iterator(true);
    }
}
