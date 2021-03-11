//package com.dipasquale.data.structure.map;
//
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.Map;
//import java.util.NavigableMap;
//import java.util.NavigableSet;
//import java.util.Set;
//import java.util.SortedMap;
//
//final class DescendingNavigableMap<TKey, TValue> implements NavigableMap<TKey, TValue> {
//    private final NavigableMap<TKey, TValue> navigableMap;
//    private final Comparator<? super TKey> comparator;
//    private final SubIterable<TKey, TValue> iterable;
//
//    DescendingNavigableMap(final NavigableMap<TKey, TValue> navigableMap, final SubIterable<TKey, TValue> iterable) {
//        this.navigableMap = navigableMap;
//        this.comparator = navigableMap.comparator().reversed();
//        this.iterable = iterable;
//    }
//
//    @Override
//    public int size() {
//        return navigableMap.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return navigableMap.isEmpty();
//    }
//
//    @Override
//    public boolean containsKey(final Object key) {
//        return navigableMap.containsKey(key);
//    }
//
//    @Override
//    public boolean containsValue(final Object value) {
//        return navigableMap.containsValue(value);
//    }
//
//    @Override
//    public TValue get(final Object key) {
//        return navigableMap.get(key);
//    }
//
//    @Override
//    public TValue put(final TKey key, final TValue value) {
//        return navigableMap.put(key, value);
//    }
//
//    @Override
//    public void putAll(final Map<? extends TKey, ? extends TValue> map) {
//        navigableMap.putAll(map);
//    }
//
//    @Override
//    public TValue remove(final Object key) {
//        return navigableMap.remove(key);
//    }
//
//    @Override
//    public void clear() {
//        navigableMap.clear();
//    }
//
//    @Override
//    public Set<TKey> keySet() {
//        return navigableMap.keySet(); // TODO: ensure reverse order
//    }
//
//    @Override
//    public Collection<TValue> values() {
//        return navigableMap.values(); // TODO: ensure reverse order
//    }
//
//    @Override
//    public Set<Entry<TKey, TValue>> entrySet() {
//        return navigableMap.entrySet(); // TODO: ensure reverse order
//    }
//
//    @Override
//    public Comparator<? super TKey> comparator() {
//        return comparator;
//    }
//
//    @Override
//    public Entry<TKey, TValue> firstEntry() {
//        return navigableMap.lastEntry();
//    }
//
//    @Override
//    public TKey firstKey() {
//        return navigableMap.lastKey();
//    }
//
//    @Override
//    public Entry<TKey, TValue> floorEntry(final TKey key) {
//        return navigableMap.ceilingEntry(key);
//    }
//
//    @Override
//    public TKey floorKey(final TKey key) {
//        return navigableMap.ceilingKey(key);
//    }
//
//    @Override
//    public Entry<TKey, TValue> lowerEntry(final TKey key) {
//        return navigableMap.higherEntry(key);
//    }
//
//    @Override
//    public TKey lowerKey(final TKey key) {
//        return navigableMap.higherKey(key);
//    }
//
//    @Override
//    public Entry<TKey, TValue> higherEntry(final TKey key) {
//        return navigableMap.lowerEntry(key);
//    }
//
//    @Override
//    public TKey higherKey(final TKey key) {
//        return navigableMap.lowerKey(key);
//    }
//
//    @Override
//    public Entry<TKey, TValue> ceilingEntry(final TKey key) {
//        return navigableMap.floorEntry(key);
//    }
//
//    @Override
//    public TKey ceilingKey(final TKey key) {
//        return navigableMap.floorKey(key);
//    }
//
//    @Override
//    public Entry<TKey, TValue> lastEntry() {
//        return navigableMap.firstEntry();
//    }
//
//    @Override
//    public TKey lastKey() {
//        return navigableMap.firstKey();
//    }
//
//    @Override
//    public Entry<TKey, TValue> pollFirstEntry() {
//        return navigableMap.pollLastEntry();
//    }
//
//    @Override
//    public Entry<TKey, TValue> pollLastEntry() {
//        return navigableMap.pollFirstEntry();
//    }
//
//    @Override
//    public NavigableMap<TKey, TValue> descendingMap() {
//        return navigableMap;
//    }
//
//    @Override
//    public NavigableSet<TKey> navigableKeySet() {
//        return navigableMap.descendingKeySet();
//    }
//
//    @Override
//    public NavigableSet<TKey> descendingKeySet() {
//        return navigableMap.navigableKeySet();
//    }
//
//    @Override
//    public NavigableMap<TKey, TValue> subMap(final TKey fromKey, final boolean fromInclusive, final TKey toKey, final boolean toInclusive) {
//        return new NavigableSubMap<>(this, fromKey, fromInclusive, toKey, toInclusive, false, iterable);
//    }
//
//    @Override
//    public SortedMap<TKey, TValue> subMap(final TKey fromKey, final TKey toKey) {
//        return subMap(fromKey, true, toKey, false);
//    }
//
//    @Override
//    public NavigableMap<TKey, TValue> headMap(final TKey toKey, final boolean inclusive) {
//        return subMap(firstKey(), true, toKey, inclusive);
//    }
//
//    @Override
//    public SortedMap<TKey, TValue> headMap(final TKey toKey) {
//        return headMap(toKey, false);
//    }
//
//    @Override
//    public NavigableMap<TKey, TValue> tailMap(final TKey fromKey, final boolean inclusive) {
//        return subMap(fromKey, inclusive, lastKey(), false);
//    }
//
//    @Override
//    public SortedMap<TKey, TValue> tailMap(final TKey fromKey) {
//        return tailMap(fromKey, true);
//    }
//}
