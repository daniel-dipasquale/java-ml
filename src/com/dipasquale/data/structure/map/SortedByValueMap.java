package com.dipasquale.data.structure.map;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.data.structure.set.InsertOrderSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
public final class SortedByValueMap<TKey, TValue> extends MapBase<TKey, TValue> {
    private final Map<TKey, Entry<TKey, TValue>> map;
    private final NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap;
    private final ObjectFactory<InsertOrderSet<Entry<TKey, TValue>>> entriesSetFactory;
    private final EntryStrategyFactory<TKey, TValue> entryStrategyFactory;
    private final KeySet<TKey, TValue> descendingKeySet = new KeySet<>(this, this::iteratorDescending);
    private final Values<TKey, TValue> descendingValues = new Values<>(this, this::iteratorDescending);
    private final EntrySet<TKey, TValue> descendingEntrySet = new EntrySet<>(this, this::iteratorDescending);

    private static <TKey, TValue> SortedByValueMap<TKey, TValue> create(final Comparator<TValue> comparator, final Map<TKey, Entry<TKey, TValue>> map, final EntryStrategyFactory<TKey, TValue> entryStrategyFactory) {
        NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap = new TreeMap<>(comparator);
        ObjectFactory<InsertOrderSet<Entry<TKey, TValue>>> entriesSetFactory = InsertOrderSet::create;

        return new SortedByValueMap<>(map, navigableMap, entriesSetFactory, entryStrategyFactory);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createHash(final Comparator<TValue> comparator) {
        ArgumentValidator.getInstance().ensureNotNull(comparator, "comparator");

        return create(comparator, new HashMap<>(), EntryHash::new);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createIdentity(final Comparator<TValue> comparator) {
        ArgumentValidator.getInstance().ensureNotNull(comparator, "comparator");

        return create(comparator, new IdentityHashMap<>(), EntryIdentity::new);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createHashConcurrent(final Comparator<TValue> comparator) {
        ArgumentValidator.getInstance().ensureNotNull(comparator, "comparator");

        Map<TKey, Entry<TKey, TValue>> map = new ConcurrentHashMap<>();
        NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap = new ConcurrentSkipListMap<>(comparator);
        ObjectFactory<InsertOrderSet<Entry<TKey, TValue>>> entriesSetFactory = InsertOrderSet::createSynchronized;
        EntryStrategyFactory<TKey, TValue> entryStrategyFactory = EntryHash::new;

        return new SortedByValueMap<>(map, navigableMap, entriesSetFactory, entryStrategyFactory);
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
    protected PutChange<? extends Entry<TKey, TValue>> putEntry(final TKey key, final TValue value) {
        PutChangeAudit audit = new PutChangeAudit();

        map.compute(key, (k, oldEntry) -> {
            if (oldEntry == null) {
                Entry<TKey, TValue> entry = createEntry(key, value);

                addToNavigableMap(entry);
                audit.entry = entry;
                audit.oldValue = null;
                audit.isNew = true;

                return entry;
            }

            if (!Objects.equals(oldEntry, value)) {
                Entry<TKey, TValue> entry = createEntry(key, value);

                removeFromNavigableMap(oldEntry);
                addToNavigableMap(entry);
                audit.entry = entry;
                audit.oldValue = oldEntry.getValue();
                audit.isNew = false;

                return entry;
            }

            audit.entry = oldEntry;
            audit.oldValue = oldEntry.getValue();
            audit.isNew = false;

            return oldEntry;
        });

        return new PutChange<Entry<TKey, TValue>>(audit.entry, audit.oldValue, audit.isNew);
    }

    @Override
    protected Entry<TKey, TValue> removeEntry(final TKey key) {
        RemoveEntryAudit removeEntryAudit = new RemoveEntryAudit();

        map.computeIfPresent(key, (k, oe) -> {
            removeFromNavigableMap(oe);
            removeEntryAudit.oldEntry = oe;

            return null;
        });

        return removeEntryAudit.oldEntry;
    }

    @Override
    public void clear() {
        map.clear();
        navigableMap.clear();
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap, final Function<InsertOrderSet<Entry<TKey, TValue>>, Stream<Entry<TKey, TValue>>> flatMapper) {
        return navigableMap.values().stream()
                .flatMap(flatMapper);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap) {
        return stream(navigableMap, Collection::stream);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> streamDescending(final NavigableMap<TValue, InsertOrderSet<Entry<TKey, TValue>>> navigableMap) {
        Function<InsertOrderSet<Entry<TKey, TValue>>, Stream<Entry<TKey, TValue>>> flatMapper = ios -> {
            Iterable<Entry<TKey, TValue>> iterable = ios::iteratorDescending;

            return StreamSupport.stream(iterable.spliterator(), false);
        };

        return stream(navigableMap.descendingMap(), flatMapper);
    }

    @Override
    protected Iterator<Entry<TKey, TValue>> iterator() {
        return stream(navigableMap).iterator();
    }

    protected Iterator<Entry<TKey, TValue>> iteratorDescending() {
        return streamDescending(navigableMap).iterator();
    }

    public Set<TKey> descendingKeySet() {
        return descendingKeySet;
    }

    public Collection<TValue> descendingValues() {
        return descendingValues;
    }

    public Set<Entry<TKey, TValue>> descendingEntrySet() {
        return descendingEntrySet;
    }

    private static <TKey, TValue> Entry<TKey, TValue> getEntry(final Entry<TValue, InsertOrderSet<Entry<TKey, TValue>>> entry, final ElementNavigator<TKey, TValue> elementNavigator) {
        if (entry == null) {
            return null;
        }

        InsertOrderSet<Entry<TKey, TValue>> keys = entry.getValue();

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

    public Set<Entry<TKey, TValue>> betweenEntrySet(final TValue from, final boolean fromInclusive, final TValue to, final boolean toInclusive) {
        return new EntrySet<>(this, stream(navigableMap.subMap(from, fromInclusive, to, toInclusive))::iterator);
    }

    public Set<Entry<TKey, TValue>> fromEntrySet(final TValue value, final boolean inclusive) {
        return new EntrySet<>(this, stream(navigableMap.tailMap(value, inclusive))::iterator);
    }

    public Set<Entry<TKey, TValue>> toStream(final TValue value, final boolean inclusive) {
        return new EntrySet<>(this, stream(navigableMap.headMap(value, inclusive))::iterator);
    }

    @FunctionalInterface
    private interface EntryStrategyFactory<TKey, TValue> {
        Entry<TKey, TValue> create(TKey key, TValue value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class EntryHash<TKey, TValue> implements Entry<TKey, TValue> {
        @EqualsAndHashCode.Include
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    private static final class EntryIdentity<TKey, TValue> implements Entry<TKey, TValue> {
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(final Object obj) {
            return key == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(key);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class PutChangeAudit {
        private Entry<TKey, TValue> entry;
        private TValue oldValue;
        private boolean isNew;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    private final class RemoveEntryAudit {
        private Entry<TKey, TValue> oldEntry;
    }

    @FunctionalInterface
    private interface ElementNavigator<TKey, TValue> {
        Entry<TKey, TValue> navigate(InsertOrderSet<Entry<TKey, TValue>> set);
    }
}