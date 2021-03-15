package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class HashDequeMap<TKey, TValue> extends AbstractMap<TKey, TValue> {
    private final Map<TKey, SimpleNode<Entry<TKey, TValue>>> nodesMap;
    private final NodeDeque<Entry<TKey, TValue>, SimpleNode<Entry<TKey, TValue>>> nodesDeque;

    private HashDequeMap(final Map<TKey, SimpleNode<Entry<TKey, TValue>>> nodesMap) {
        this.nodesMap = nodesMap;
        this.nodesDeque = new SimpleNodeDeque<>();
    }

    public HashDequeMap() {
        this(new HashMap<>());
    }

    public HashDequeMap(final int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }

    @Override
    public int size() {
        return nodesMap.size();
    }

    @Override
    public boolean containsKey(final Object key) {
        return nodesMap.containsKey(key);
    }

    @Override
    public TValue get(final Object key) {
        SimpleNode<Entry<TKey, TValue>> node = nodesMap.get(key);

        if (node == null) {
            return null;
        }

        return nodesDeque.getValue(node).getValue();
    }

    private TValue put(final TKey key, final TValue value, final NodeReplacer<TKey, TValue> nodeReplacer) {
        SimpleNode<Entry<TKey, TValue>> nodeOld = nodesMap.get(key);

        if (nodeOld == null) {
            SimpleNode<Entry<TKey, TValue>> nodeNew = nodesDeque.createUnbound(new EntryInternal<>(key, value));

            nodesDeque.add(nodeNew);
            nodesMap.put(key, nodeNew);

            return null;
        }

        if (nodeReplacer != null) {
            nodeReplacer.replace(nodeOld);
        }

        return nodesDeque.getValue(nodeOld).setValue(value);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        return put(key, value, null);
    }

    public TValue putFirst(final TKey key, final TValue value) {
        return put(key, value, nodesDeque::offerFirst);
    }

    public TValue putLast(final TKey key, final TValue value) {
        return put(key, value, nodesDeque::offerLast);
    }

    @Override
    public TValue remove(final Object key) {
        SimpleNode<Entry<TKey, TValue>> nodeOld = nodesMap.remove(key);

        if (nodeOld == null) {
            return null;
        }

        nodesDeque.remove(nodeOld);

        return nodesDeque.getValue(nodeOld).getValue();
    }

    private TValue remove(final NodeRemover<TKey, TValue> nodeRemover) {
        SimpleNode<Entry<TKey, TValue>> node = nodeRemover.remove();

        if (node == null) {
            return null;
        }

        Entry<TKey, TValue> entry = nodesDeque.getValue(node);

        nodesMap.remove(entry.getKey());

        return entry.getValue();
    }

    public TValue removeFirst() {
        return remove(nodesDeque::removeFirst);
    }

    public TValue removeLast() {
        return remove(nodesDeque::removeLast);
    }

    @Override
    public void clear() {
        nodesMap.clear();
        nodesDeque.clear();
    }

    @Override
    protected Iterator<? extends Entry<TKey, TValue>> iterator() {
        return nodesDeque.stream()
                .map(nodesDeque::getValue)
                .iterator();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode
    private static final class EntryInternal<TKey, TValue> implements Entry<TKey, TValue> {
        private final TKey key;
        private TValue value;

        @Override
        public TValue setValue(final TValue newValue) {
            TValue oldValue = value;

            value = newValue;

            return oldValue;
        }
    }

    @FunctionalInterface
    private interface NodeReplacer<TKey, TValue> {
        boolean replace(SimpleNode<Entry<TKey, TValue>> node);
    }

    @FunctionalInterface
    private interface NodeRemover<TKey, TValue> {
        SimpleNode<Entry<TKey, TValue>> remove();
    }
}
