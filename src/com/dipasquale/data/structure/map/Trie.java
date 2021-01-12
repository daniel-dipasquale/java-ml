package com.dipasquale.data.structure.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class Trie<TKey, TValue> extends MapBase<TKey, TValue> {
    private final TrieKeyTokenizer<TKey> keyTokenizer;
    private final InternalEntry rootEntry = new InternalEntry(null);
    private int size = 0;

    protected Map<Object, InternalEntry> createMap() {
        return new HashMap<>();
    }

    @Override
    public int size() {
        return size;
    }

    private InternalEntry getTrieEntry(final TKey key) {
        InternalEntry entry = rootEntry;

        for (Object tokenizedKey : keyTokenizer.tokenize(key)) {
            entry = entry.entries.get(tokenizedKey);

            if (entry == null) {
                return null;
            }
        }

        return entry;
    }

    @Override
    public boolean containsKey(final Object key) {
        return getTrieEntry((TKey) key) != null;
    }

    @Override
    public TValue get(final Object key) {
        InternalEntry entry = getTrieEntry((TKey) key);

        if (entry == null) {
            return null;
        }

        return entry.value;
    }

    private PutChange<InternalEntry> putTrieEntry(final TKey key, final TValue value) {
        InternalEntry entry = rootEntry;

        for (Object tokenizedKey : keyTokenizer.tokenize(key)) {
            entry = entry.entries.computeIfAbsent(tokenizedKey, InternalEntry::new);
        }

        TValue oldValue = entry.value;

        entry.key = key;
        entry.value = value;
        size++;

        return new PutChange<InternalEntry>(entry, oldValue, oldValue == null);
    }

    @Override
    protected PutChange<? extends Entry<TKey, TValue>> putEntry(final TKey key, final TValue value) {
        return putTrieEntry(key, value);
    }

    private InternalEntry removeTrieEntry(final TKey key) {
        Stack<InternalEntry> entries = new Stack<>();
        InternalEntry entry = rootEntry;

        for (Object tokenizedKey : keyTokenizer.tokenize(key)) {
            entries.push(entry);
            entry = entry.entries.get(tokenizedKey);

            if (entry == null) {
                return null;
            }
        }

        if (entries.isEmpty()) {
            return null;
        }

        InternalEntry entryRemoved = new InternalEntry(null);

        entryRemoved.key = entry.key;
        entryRemoved.value = entry.value;
        entry.key = null;
        entry.value = null;

        InternalEntry entryPrevious = entries.pop();

        while (entryPrevious != null && entryPrevious.entries.size() == 1 && entryPrevious.value == null) {
            entryPrevious.entries = null;
            entry = entryPrevious;
            entryPrevious = entries.pop();
        }

        if (entryPrevious != null) {
            entryPrevious.entries.remove(entry.tokenizedKey);
        }

        size--;

        return entryRemoved;
    }

    @Override
    protected Entry<TKey, TValue> removeEntry(final TKey key) {
        return removeTrieEntry(key);
    }

    @Override
    public void clear() {
        rootEntry.entries.clear();
        size = 0;
    }

    private Stream<Entry<TKey, TValue>> stream(final InternalEntry entry) {
        Stream<Entry<TKey, TValue>> streamSingle = Optional.of(entry)
                .filter(e -> e.value != null)
                .map(e -> (Entry<TKey, TValue>) e)
                .stream();

        Stream<Entry<TKey, TValue>> streamEntries = Optional.of(entry)
                .map(e -> e.entries)
                .map(e -> e.entrySet().stream()
                        .flatMap(ei -> stream(ei.getValue())))
                .orElseGet(Stream::empty);

        return Stream.concat(streamSingle, streamEntries);
    }

    @Override
    public Iterator<Entry<TKey, TValue>> iterator() {
        return stream(rootEntry).iterator();
    }

    @RequiredArgsConstructor
    private final class InternalEntry implements Entry<TKey, TValue> {
        private final Object tokenizedKey;
        private Map<Object, InternalEntry> entries = createMap();
        @Getter
        private TKey key;
        @Getter
        private TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }
}
