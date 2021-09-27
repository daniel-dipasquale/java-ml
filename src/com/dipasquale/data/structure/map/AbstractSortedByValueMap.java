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

public abstract class AbstractSortedByValueMap<TKey, TValue> extends AbstractMap<TKey, TValue> implements SortedByValueMap<TKey, TValue>, Serializable {
    @Serial
    private static final long serialVersionUID = -6446020115083134726L;
    private final ProxyIteratorFactory<TKey, TValue> iteratorFactory;
    private final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory;
    private final EntryStrategyFactory<TKey, TValue> entryStrategyFactory;
    private final Set<TKey> descendingKeySet;

    private AbstractSortedByValueMap(final ProxyIteratorFactory<TKey, TValue> iteratorFactory, final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory, final EntryStrategyFactory<TKey, TValue> entryStrategyFactory) {
        super(iteratorFactory);
        this.iteratorFactory = initializeIteratorFactory(iteratorFactory, this);
        this.entriesSetFactory = entriesSetFactory;
        this.entryStrategyFactory = entryStrategyFactory;
        this.descendingKeySet = new MapKeySet<>(this, initializeDescendingIteratorFactory(new ProxyIteratorFactory<>(), this));
    }

    protected AbstractSortedByValueMap(final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory, final EntryStrategyFactory<TKey, TValue> entryStrategyFactory) {
        this(new ProxyIteratorFactory<>(), entriesSetFactory, entryStrategyFactory);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap, final Function<DequeSet<Entry<TKey, TValue>>, Stream<Entry<TKey, TValue>>> flatMapper) {
        return navigableMap.values().stream()
                .flatMap(flatMapper);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> stream(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap) {
        return stream(navigableMap, Collection::stream);
    }

    private static <TKey, TValue> Stream<Entry<TKey, TValue>> descendingStream(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap) {
        return stream(navigableMap.descendingMap(), ios -> {
            Spliterator<Entry<TKey, TValue>> spliterator = Spliterators.spliteratorUnknownSize(ios.descendingIterator(), 0);

            return StreamSupport.stream(spliterator, false);
        });
    }

    protected static <TKey, TValue> ProxyIteratorFactory<TKey, TValue> initializeIteratorFactory(final ProxyIteratorFactory<TKey, TValue> iteratorFactory, final AbstractSortedByValueMap<TKey, TValue> map) {
        iteratorFactory.underlying = (IteratorFactory<TKey, TValue> & Serializable) () -> stream(map.getStorage().navigableMap).iterator();

        return iteratorFactory;
    }

    protected static <TKey, TValue> ProxyIteratorFactory<TKey, TValue> initializeDescendingIteratorFactory(final ProxyIteratorFactory<TKey, TValue> iteratorFactory, final AbstractSortedByValueMap<TKey, TValue> map) {
        iteratorFactory.underlying = (IteratorFactory<TKey, TValue> & Serializable) () -> descendingStream(map.getStorage().navigableMap).iterator();

        return iteratorFactory;
    }

    protected static <TKey, TValue> Storage<TKey, TValue> createStorage(final MapFactory<TKey, TValue> mapFactory, final NavigableMapFactory<TKey, TValue> navigableMapFactory) {
        return new Storage<>(mapFactory.create(), navigableMapFactory.create());
    }

    protected abstract Storage<TKey, TValue> getStorage();

    @Override
    public Set<TKey> descendingKeySet() {
        return descendingKeySet;
    }

    @Override
    public int size() {
        return getStorage().map.size();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getStorage().map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return getStorage().navigableMap.containsKey(value);
    }

    private static <TKey, TValue> TValue extractValue(final Entry<TKey, TValue> entry) {
        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    @Override
    public TValue get(final Object key) {
        return extractValue(getStorage().map.get(key));
    }

    private static <TKey, TValue> void removeFrom(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap, final Entry<TKey, TValue> entry) {
        Set<Entry<TKey, TValue>> entries = navigableMap.get(entry.getValue());

        entries.remove(entry);

        if (entries.isEmpty()) {
            navigableMap.remove(entry.getValue());
        }
    }

    private static <TKey, TValue> void addTo(final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap, final Entry<TKey, TValue> entry, final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory) {
        Set<Entry<TKey, TValue>> entries = navigableMap.computeIfAbsent(entry.getValue(), k -> entriesSetFactory.create());

        entries.add(entry);
    }

    private Entry<TKey, TValue> createEntry(final TKey key, final TValue value) {
        return entryStrategyFactory.create(key, value);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        Storage<TKey, TValue> storage = getStorage();
        Entry<TKey, TValue> entryOld = storage.map.get(key);

        if (entryOld == null) {
            Entry<TKey, TValue> entryNew = createEntry(key, value);

            addTo(storage.navigableMap, entryNew, entriesSetFactory);
            storage.map.put(key, entryNew);

            return null;
        }

        if (!Objects.equals(entryOld.getValue(), value)) {
            Entry<TKey, TValue> entryNew = createEntry(key, value);

            removeFrom(storage.navigableMap, entryOld);
            addTo(storage.navigableMap, entryNew, entriesSetFactory);
            storage.map.put(key, entryNew);
        }

        return entryOld.getValue();
    }

    private static <TKey, TValue> TValue remove(final Storage<TKey, TValue> storage, final TKey key) {
        Entry<TKey, TValue> entryOld = storage.map.remove(key);

        if (entryOld == null) {
            return null;
        }

        removeFrom(storage.navigableMap, entryOld);

        return entryOld.getValue();
    }

    @Override
    public TValue remove(final Object key) {
        return remove(getStorage(), (TKey) key);
    }

    @Override
    public void clear() {
        getStorage().clear();
    }

    private static <TKey, TValue> Entry<TKey, TValue> getEntry(final Entry<TValue, DequeSet<Entry<TKey, TValue>>> entry, final EntryNavigator<TKey, TValue> entryNavigator) {
        if (entry == null) {
            return null;
        }

        DequeSet<Entry<TKey, TValue>> keys = entry.getValue();

        return entryNavigator.navigate(keys);
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
        return getEntry(getStorage().navigableMap.firstEntry(), DequeSet::getFirst);
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
        return getEntry(getStorage().navigableMap.lastEntry(), DequeSet::getLast);
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
    protected interface MapFactory<TKey, TValue> extends ObjectFactory<Map<TKey, Entry<TKey, TValue>>>, Serializable {
    }

    @FunctionalInterface
    protected interface NavigableMapFactory<TKey, TValue> extends ObjectFactory<NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>>>, Serializable {
    }

    @FunctionalInterface
    protected interface EntryStrategyFactory<TKey, TValue> extends Serializable {
        Entry<TKey, TValue> create(TKey key, TValue value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected static final class Storage<TKey, TValue> implements Serializable {
        @Serial
        private static final long serialVersionUID = -4400114009879067230L;
        private final Map<TKey, Entry<TKey, TValue>> map;
        private final NavigableMap<TValue, DequeSet<Entry<TKey, TValue>>> navigableMap;

        void clear() {
            map.clear();
            navigableMap.clear();
        }
    }

    @FunctionalInterface
    private interface EntryNavigator<TKey, TValue> {
        Entry<TKey, TValue> navigate(DequeSet<Entry<TKey, TValue>> set);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ProxyIteratorFactory<TKey, TValue> implements IteratorFactory<TKey, TValue> {
        @Serial
        private static final long serialVersionUID = -3776208100159642493L;
        private IteratorFactory<TKey, TValue> underlying = null;

        @Override
        public Iterator<Entry<TKey, TValue>> iterator() {
            return underlying.iterator();
        }
    }
}