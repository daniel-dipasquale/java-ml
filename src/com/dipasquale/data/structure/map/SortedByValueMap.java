package com.dipasquale.data.structure.map;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.data.structure.set.InsertOrderSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SortedByValueMap<TKey, TValue> extends MapBase<TKey, TValue> {
    private final Map<TKey, EntryInternal> map;
    private final NavigableMap<TValue, InsertOrderSet<EntryInternal>> navigableMap;
    private final ObjectFactory<InsertOrderSet<EntryInternal>> entriesSetFactory;

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> create(final Comparator<TValue> comparator) {
        ArgumentValidator.getInstance().ensureNotNull(comparator, "comparator");

        Map<TKey, SortedByValueMap<TKey, TValue>.EntryInternal> map = new HashMap<>();
        NavigableMap<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> navigableMap = new TreeMap<>(comparator);
        ObjectFactory<InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> entriesSetFactory = InsertOrderSet::create;

        return new SortedByValueMap<TKey, TValue>(map, navigableMap, entriesSetFactory);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createConcurrent(final Comparator<TValue> comparator) {
        ArgumentValidator.getInstance().ensureNotNull(comparator, "comparator");

        Map<TKey, SortedByValueMap<TKey, TValue>.EntryInternal> map = new ConcurrentHashMap<>();
        NavigableMap<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> navigableMap = new ConcurrentSkipListMap<>(comparator);
        ObjectFactory<InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> entriesSetFactory = InsertOrderSet::createSynchronized;

        return new SortedByValueMap<TKey, TValue>(map, navigableMap, entriesSetFactory);
    }

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

    private static <TKey, TValue> TValue extractValue(final SortedByValueMap<TKey, TValue>.EntryInternal entry) {
        if (entry == null) {
            return null;
        }

        return entry.value;
    }

    @Override
    public TValue get(final Object key) {
        return extractValue(map.get(key));
    }

    private void removeFromNavigableMap(final EntryInternal entry) {
        Set<EntryInternal> entries = navigableMap.get(entry.value);

        entries.remove(entry);

        if (entries.isEmpty()) {
            navigableMap.remove(entry.value);
        }
    }

    private void addToNavigableMap(final EntryInternal entry) {
        Set<EntryInternal> entries = navigableMap.computeIfAbsent(entry.value, k -> entriesSetFactory.create());

        entries.add(entry);
    }

    @Override
    protected PutChange<? extends Entry<TKey, TValue>> putEntry(final TKey key, final TValue value) {
        Object[] output = new Object[3];

        map.compute(key, (k, oldEntry) -> {
            if (oldEntry == null) {
                EntryInternal entry = new EntryInternal(key, value);

                addToNavigableMap(entry);
                output[0] = entry;
                output[1] = null;
                output[2] = true;

                return entry;
            }

            if (!Objects.equals(oldEntry, value)) {
                EntryInternal entry = new EntryInternal(key, value);

                removeFromNavigableMap(oldEntry);
                addToNavigableMap(entry);
                output[0] = entry;
                output[1] = oldEntry.value;
                output[2] = false;

                return entry;
            }

            output[0] = oldEntry;
            output[1] = oldEntry.value;
            output[2] = false;

            return oldEntry;
        });

        EntryInternal entry = (EntryInternal) output[0];
        TValue oldValue = (TValue) output[1];
        boolean isNew = (boolean) output[2];

        return new PutChange<EntryInternal>(entry, oldValue, isNew);
    }

    @Override
    protected Entry<TKey, TValue> removeEntry(final TKey key) {
        Object[] output = new Object[1];

        map.computeIfPresent(key, (k, oe) -> {
            removeFromNavigableMap(oe);
            output[0] = oe;

            return null;
        });

        return (Entry<TKey, TValue>) output[0];
    }

    @Override
    public void clear() {
        map.clear();
        navigableMap.clear();
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> navigableMap, final Function<InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>, Stream<SortedByValueMap<TKey, TValue>.EntryInternal>> flatMapper) {
        return navigableMap.values().stream()
                .flatMap(flatMapper);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> navigableMap) {
        return stream(navigableMap, Collection::stream);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> streamDescending(final NavigableMap<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> navigableMap) {
        Function<InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>, Stream<SortedByValueMap<TKey, TValue>.EntryInternal>> flatMapper = ios -> {
            Iterable<SortedByValueMap<TKey, TValue>.EntryInternal> iterable = ios::iteratorDescending;

            return StreamSupport.stream(iterable.spliterator(), false);
        };

        return stream(navigableMap.descendingMap(), flatMapper);
    }

    @Override
    protected Iterator<Entry<TKey, TValue>> iterator() {
        return stream(navigableMap).iterator();
    }

    private static <TKey, TValue> Entry<TKey, TValue> getEntry(final Entry<TValue, InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal>> entry, final ElementNavigator<TKey, TValue> elementNavigator) {
        if (entry == null) {
            return null;
        }

        InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal> keys = entry.getValue();

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

    public Entry<TKey, TValue> headEntry() {
        return getEntry(navigableMap.firstEntry(), InsertOrderSet::first);
    }

    public TKey headKey() {
        return getKey(headEntry());
    }

    public TValue headValue() {
        return getValue(headEntry());
    }

    public Entry<TKey, TValue> tailEntry() {
        return getEntry(navigableMap.lastEntry(), InsertOrderSet::last);
    }

    public TKey tailKey() {
        return getKey(tailEntry());
    }

    public TValue tailValue() {
        return getValue(tailEntry());
    }

    public Stream<Entry<TKey, TValue>> between(final TValue from, final boolean fromInclusive, final TValue to, final boolean toInclusive) {
        return stream(navigableMap.subMap(from, fromInclusive, to, toInclusive));
    }

    public Stream<Entry<TKey, TValue>> from(final TValue value, final boolean inclusive) {
        return stream(navigableMap.tailMap(value, inclusive));
    }

    public Stream<Entry<TKey, TValue>> to(final TValue value, final boolean inclusive) {
        return stream(navigableMap.headMap(value, inclusive));
    }

    public Stream<Entry<TKey, TValue>> descending() {
        return streamDescending(navigableMap);
    }

    @FunctionalInterface
    private interface ElementNavigator<TKey, TValue> {
        SortedByValueMap<TKey, TValue>.EntryInternal navigate(InsertOrderSet<SortedByValueMap<TKey, TValue>.EntryInternal> set);
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private final class EntryInternal implements Entry<TKey, TValue> {
        @EqualsAndHashCode.Include
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }
}