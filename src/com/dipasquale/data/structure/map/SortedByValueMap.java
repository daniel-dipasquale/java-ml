package com.dipasquale.data.structure.map;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.data.structure.set.DequeSet;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SortedByValueMap<TKey, TValue> extends MapBase<TKey, TValue> { // TODO: should implement NavigableMap
    private final Map<TKey, Entry<TKey, TValue>> map;
    private final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap;
    private final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory;
    private final EntryStrategyFactory<TKey, TValue> entryStrategyFactory;
    private final KeySet<TKey, TValue> descendingKeySet = new KeySetCustomIterable<>(this, this::iteratorDescending);

    private static <TKey, TValue> SortedByValueMap<TKey, TValue> create(final Comparator<TValue> comparator, final Map<TKey, Entry<TKey, TValue>> map, final EntryStrategyFactory<TKey, TValue> entryStrategyFactory) {
        NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap = new TreeMap<>(comparator);
        ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory = DequeSet::create;

        return new SortedByValueMap<>(map, navigableMap, entriesSetFactory, entryStrategyFactory);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createHash(final Comparator<TValue> comparator) {
        ArgumentValidator.ensureNotNull(comparator, "comparator");

        return create(comparator, new HashMap<>(), EntryHash::new);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createIdentity(final Comparator<TValue> comparator) {
        ArgumentValidator.ensureNotNull(comparator, "comparator");

        return create(comparator, new IdentityHashMap<>(), EntryIdentity::new);
    }

    public static <TKey, TValue> SortedByValueMap<TKey, TValue> createHashConcurrent(final Comparator<TValue> comparator) {
        ArgumentValidator.ensureNotNull(comparator, "comparator");

        Map<TKey, Entry<TKey, TValue>> map = new ConcurrentHashMap<>();
        NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap = new ConcurrentSkipListMap<>(comparator);
        ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory = DequeSet::createSynchronized;
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

    public Entry<TKey, TValue> headEntry() {
        return getEntry(navigableMap.firstEntry(), DequeSet::getFirst);
    }

    public TKey headKey() {
        return getKey(headEntry());
    }

    public TValue headValue() {
        return getValue(headEntry());
    }

    public Entry<TKey, TValue> tailEntry() {
        return getEntry(navigableMap.lastEntry(), DequeSet::getLast);
    }

    public TKey tailKey() {
        return getKey(tailEntry());
    }

    public TValue tailValue() {
        return getValue(tailEntry());
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
    private final class ChangeAudit {
        private TValue oldValue;
    }

    @FunctionalInterface
    private interface ElementNavigator<TKey, TValue> {
        Entry<TKey, TValue> navigate(DequeSet<Entry<TKey, TValue>> set);
    }
}