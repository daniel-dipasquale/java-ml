//package com.dipasquale.data.structure.map;
//
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.NavigableMap;
//import java.util.NavigableSet;
//import java.util.SortedSet;
//import java.util.stream.StreamSupport;
//
//final class NavigableKeySet<TKey, TValue> extends KeySet<TKey, TValue> implements NavigableSet<TKey> {
//    private final NavigableMapBase<TKey, TValue> navigableMap;
//    private final OrderedIterable<TKey, TValue> iterableOrdered;
//    private final boolean ascending;
//    private final SubIterable<TKey, TValue> iterableSub;
//
//    NavigableKeySet(final NavigableMapBase<TKey, TValue> navigableMap, final OrderedIterable<TKey, TValue> iterableOrdered, final boolean ascending, final SubIterable<TKey, TValue> iterableSub) {
//        super(navigableMap);
//        this.navigableMap = navigableMap;
//        this.iterableOrdered = iterableOrdered;
//        this.ascending = ascending;
//        this.iterableSub = iterableSub;
//    }
//
//    @Override
//    public Comparator<? super TKey> comparator() {
//        return navigableMap.comparator();
//    }
//
//    @Override
//    public TKey first() {
//        return navigableMap.firstKey();
//    }
//
//    @Override
//    public TKey floor(final TKey key) {
//        return navigableMap.floorKey(key);
//    }
//
//    @Override
//    public TKey lower(final TKey key) {
//        return navigableMap.lowerKey(key);
//    }
//
//    @Override
//    public TKey higher(final TKey key) {
//        return navigableMap.higherKey(key);
//    }
//
//    @Override
//    public TKey ceiling(final TKey key) {
//        return navigableMap.ceilingKey(key);
//    }
//
//    @Override
//    public TKey last() {
//        return navigableMap.lastKey();
//    }
//
//    private static <TKey, TValue> TKey getKey(final Map.Entry<TKey, TValue> entry) {
//        if (entry == null) {
//            return null;
//        }
//
//        return entry.getKey();
//    }
//
//    @Override
//    public TKey pollFirst() {
//        return getKey(navigableMap.pollFirstEntry());
//    }
//
//    @Override
//    public TKey pollLast() {
//        return getKey(navigableMap.pollLastEntry());
//    }
//
//    private Iterator<TKey> iterator(final boolean asc) {
//        Iterable<Map.Entry<TKey, TValue>> entries = () -> iterableOrdered.iterator(ascending && asc);
//
//        return StreamSupport.stream(entries.spliterator(), false)
//                .map(Map.Entry::getKey)
//                .iterator();
//    }
//
//    @Override
//    public Iterator<TKey> iterator() {
//        return iterator(true);
//    }
//
//    @Override
//    public Iterator<TKey> descendingIterator() {
//        return iterator(false);
//    }
//
//    @Override
//    public NavigableSet<TKey> descendingSet() {
//        return navigableMap.descendingMap().navigableKeySet();
//    }
//
//    @Override
//    public NavigableSet<TKey> subSet(final TKey fromElement, final boolean fromInclusive, final TKey toElement, final boolean toInclusive) {
//        return new NavigableSubMap<>(navigableMap, fromElement, fromInclusive, toElement, toInclusive, ascending, iterableSub).navigableKeySet();
//    }
//
//    @Override
//    public SortedSet<TKey> subSet(final TKey fromElement, final TKey toElement) {
//        return subSet(fromElement, true, toElement, false);
//    }
//
//    @Override
//    public NavigableSet<TKey> headSet(final TKey toElement, final boolean inclusive) {
//        return subSet(first(), true, toElement, inclusive);
//    }
//
//    @Override
//    public SortedSet<TKey> headSet(final TKey toElement) {
//        return headSet(toElement, false);
//    }
//
//    @Override
//    public NavigableSet<TKey> tailSet(final TKey fromElement, final boolean inclusive) {
//        return subSet(fromElement, inclusive, last(), false);
//    }
//
//    @Override
//    public SortedSet<TKey> tailSet(final TKey fromElement) {
//        return tailSet(fromElement, true);
//    }
//}
