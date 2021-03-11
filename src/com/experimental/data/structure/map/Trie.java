package com.experimental.data.structure.map;

import com.dipasquale.data.structure.map.MapBase;
import lombok.AccessLevel;
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
    private final EntryInternal rootEntry = new EntryInternal(null);
    private int size = 0;

    protected Map<Object, EntryInternal> createMap() {
        return new HashMap<>();
    }

    @Override
    public int size() {
        return size;
    }

    private EntryInternal getTrieEntry(final TKey key) {
        EntryInternal entry = rootEntry;

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
        EntryInternal entry = getTrieEntry((TKey) key);

        if (entry == null) {
            return null;
        }

        return entry.value;
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        EntryInternal entry = rootEntry;

        for (Object tokenizedKey : keyTokenizer.tokenize(key)) {
            entry = entry.entries.computeIfAbsent(tokenizedKey, EntryInternal::new);
        }

        TValue oldValue = entry.value;

        entry.key = key;
        entry.value = value;
        size++;

        return oldValue;
    }

    @Override
    public TValue remove(final Object key) {
        Stack<EntryInternal> entries = new Stack<>();
        EntryInternal entry = rootEntry;

        for (Object tokenizedKey : keyTokenizer.tokenize((TKey) key)) {
            entries.push(entry);
            entry = entry.entries.get(tokenizedKey);

            if (entry == null) {
                return null;
            }
        }

        if (entries.isEmpty()) {
            return null;
        }

        EntryInternal entryRemoved = new EntryInternal(null);

        entryRemoved.key = entry.key;
        entryRemoved.value = entry.value;
        entry.key = null;
        entry.value = null;

        EntryInternal entryPrevious = entries.pop();

        while (entryPrevious != null && entryPrevious.entries.size() == 1 && entryPrevious.value == null) {
            entryPrevious.entries = null;
            entry = entryPrevious;
            entryPrevious = entries.pop();
        }

        if (entryPrevious != null) {
            entryPrevious.entries.remove(entry.tokenizedKey);
        }

        size--;

        return entryRemoved.value;
    }

    @Override
    public void clear() {
        rootEntry.entries.clear();
        size = 0;
    }

    private Stream<Entry<TKey, TValue>> stream(final EntryInternal entry) {
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
    protected Iterator<? extends Entry<TKey, TValue>> iterator() {
        return stream(rootEntry).iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class EntryInternal implements Entry<TKey, TValue> {
        private final Object tokenizedKey;
        private Map<Object, EntryInternal> entries = createMap();
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
