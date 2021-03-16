package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractDequeMap<TKey, TValue, TNode extends Node> extends AbstractMap<TKey, TValue> implements DequeMap<TKey, TValue> {
    private final Map<TKey, TNode> nodesMap;
    private final NodeDeque<Entry<TKey, TValue>, TNode> nodesDeque;

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
        TNode node = nodesMap.get(key);

        if (node == null) {
            return null;
        }

        return nodesDeque.getValue(node).getValue();
    }

    private TValue put(final TKey key, final TValue value, final NodeReplacer<TNode> nodeReplacer) {
        TNode nodeOld = nodesMap.get(key);

        if (nodeOld == null) {
            TNode nodeNew = nodesDeque.createUnbound(new EntryInternal<>(key, value));

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
        TNode nodeOld = nodesMap.remove(key);

        if (nodeOld == null) {
            return null;
        }

        nodesDeque.remove(nodeOld);

        return nodesDeque.getValue(nodeOld).getValue();
    }

    private Entry<TKey, TValue> removeFromMap(final TNode node) {
        Entry<TKey, TValue> entry = nodesDeque.getValue(node);

        nodesMap.remove(entry.getKey());

        return entry;
    }

    private TValue removeFromMapAndGetValue(final TNode node) {
        if (node == null) {
            return null;
        }

        return removeFromMap(node).getValue();
    }

    private Entry<TKey, TValue> removeFromMapAndGetEntry(final TNode node) {
        if (node == null) {
            return null;
        }

        return removeFromMap(node);
    }

    public TValue removeFirst() {
        return removeFromMapAndGetValue(nodesDeque.removeFirst());
    }

    public TValue removeLast() {
        return removeFromMapAndGetValue(nodesDeque.removeLast());
    }

    public Entry<TKey, TValue> withdrawFirst() {
        return removeFromMapAndGetEntry(nodesDeque.removeFirst());
    }

    public Entry<TKey, TValue> withdrawLast() {
        return removeFromMapAndGetEntry(nodesDeque.removeLast());
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
    private interface NodeReplacer<T extends Node> {
        boolean replace(T node);
    }
}
