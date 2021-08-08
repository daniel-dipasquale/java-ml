package com.dipasquale.data.structure.map;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractSortedByValueMap<TKey, TValue> extends AbstractMap<TKey, TValue> implements SortedByValueMap<TKey, TValue>, Serializable {
    @Serial
    private static final long serialVersionUID = 2986439092892441609L;
    private final Map<TKey, Entry<TKey, TValue>> map;
    private final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap;
    private final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory;
    private final EntryStrategyFactory<TKey, TValue> entryStrategyFactory;
    private final MapKeySet<TKey, TValue> descendingKeySet = new MapKeySetIterableProxy<>(this, (Iterable<Map.Entry<TKey, TValue>> & Serializable) this::iteratorDescending);

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return navigableMap.containsKey(value);
    }

    private static <TKey, TValue> TValue extractValue(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    @Override
    public TValue get(final Object key) {
        return extractValue(map.get(key));
    }

    private void removeFromNavigableMap(final Entry<TKey, TValue> entry) {
        Set<Entry<TKey, TValue>> entries = navigableMap.get(entry.getValue());

        entries.remove(entry);

        if (entries.isEmpty()) {
            navigableMap.remove(entry.getValue());
        }
    }

    private void addToNavigableMap(final Entry<TKey, TValue> entry) {
        Set<Entry<TKey, TValue>> entries = navigableMap.computeIfAbsent(entry.getValue(), k -> entriesSetFactory.create());

        entries.add(entry);
    }

    private Entry<TKey, TValue> createEntry(final TKey key, final TValue value) {
        return entryStrategyFactory.create(key, value);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        ChangeAudit audit = new ChangeAudit();

        map.compute(key, (k, oe) -> {
            if (oe == null) {
                Entry<TKey, TValue> entry = createEntry(key, value);

                addToNavigableMap(entry);
                audit.oldValue = null;

                return entry;
            }

            if (!Objects.equals(oe.getValue(), value)) {
                Entry<TKey, TValue> entry = createEntry(key, value);

                removeFromNavigableMap(oe);
                addToNavigableMap(entry);
                audit.oldValue = oe.getValue();

                return entry;
            }

            audit.oldValue = oe.getValue();

            return oe;
        });

        return audit.oldValue;
    }

    @Override
    public TValue remove(final Object key) {
        ChangeAudit audit = new ChangeAudit();

        map.computeIfPresent((TKey) key, (k, oe) -> {
            removeFromNavigableMap(oe);
            audit.oldValue = oe.getValue();

            return null;
        });

        return audit.oldValue;
    }

    @Override
    public void clear() {
        map.clear();
        navigableMap.clear();
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap, final Function<DequeSet<Entry<TKey, TValue>>, Stream<Entry<TKey, TValue>>> flatMapper) {
        return navigableMap.values().stream()
                .flatMap(flatMapper);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap) {
        return stream(navigableMap, Collection::stream);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> streamDescending(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap) {
        Function<DequeSet<Entry<TKey, TValue>>, Stream<Entry<TKey, TValue>>> flatMapper = ios -> {
            Spliterator<Entry<TKey, TValue>> spliterator = Spliterators.spliteratorUnknownSize(ios.descendingIterator(), 0);

            return StreamSupport.stream(spliterator, false);
        };

        return stream(navigableMap.descendingMap(), flatMapper);
    }

    @Override
    protected Iterator<? extends Entry<TKey, TValue>> iterator() {
        return stream(navigableMap).iterator();
    }

    protected Iterator<Entry<TKey, TValue>> iteratorDescending() {
        return streamDescending(navigableMap).iterator();
    }

    @Override
    public Set<TKey> descendingKeySet() {
        return descendingKeySet;
    }

    private static <TKey, TValue> Entry<TKey, TValue> getEntry(final Entry<TValue, DequeSet<Entry<TKey, TValue>>> entry, final ElementNavigator<TKey, TValue> elementNavigator) {
        if (entry == null) {
            return null;
        }

        DequeSet<Entry<TKey, TValue>> keys = entry.getValue();

        return elementNavigator.navigate(keys);
    }

    private static <TKey, TValue> TKey getKey(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            throw new NoSuchElementException();
        }

        return entry.getKey();
    }

    private static <TKey, TValue> TValue getValue(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            throw new NoSuchElementException();
        }

        return entry.getValue();
    }

    @Override
    public Entry<TKey, TValue> headEntry() {
        return getEntry(navigableMap.firstEntry(), DequeSet::getFirst);
    }

    @Override
    public TKey headKey() {
        return getKey(headEntry());
    }

    @Override
    public TValue headValue() {
        return getValue(headEntry());
    }

    @Override
    public Entry<TKey, TValue> tailEntry() {
        return getEntry(navigableMap.lastEntry(), DequeSet::getLast);
    }

    @Override
    public TKey tailKey() {
        return getKey(tailEntry());
    }

    @Override
    public TValue tailValue() {
        return getValue(tailEntry());
    }

    @FunctionalInterface
    protected interface EntryStrategyFactory<TKey, TValue> {
        Entry<TKey, TValue> create(TKey key, TValue value);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ChangeAudit {
        private TValue oldValue;
    }

    @FunctionalInterface
    private interface ElementNavigator<TKey, TValue> {
        Entry<TKey, TValue> navigate(DequeSet<Entry<TKey, TValue>> set);
    }
}