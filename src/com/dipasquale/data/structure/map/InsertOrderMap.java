package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class InsertOrderMap<TKey, TValue> extends MapBase<TKey, TValue> {
    private final Map<TKey, Node> nodesMap;
    private final NodeDeque<Entry<TKey, TValue>> nodesQueue;

    public InsertOrderMap() {
        this.nodesMap = new HashMap<>();
        this.nodesQueue = NodeDeque.create();
    }

    public InsertOrderMap(final int initialCapacity) {
        this.nodesMap = new HashMap<>(initialCapacity);
        this.nodesQueue = NodeDeque.create();
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
        Node node = nodesMap.get(key);

        if (node == null) {
            return null;
        }

        return nodesQueue.getValue(node).getValue();
    }

    private TValue put(final TKey key, final TValue value, final NodeReplacer nodeReplacer) {
        Node nodeOld = nodesMap.get(key);

        if (nodeOld == null) {
            Node nodeNew = nodesQueue.createUnbound(new EntryInternal<>(key, value));

            nodesQueue.add(nodeNew);
            nodesMap.put(key, nodeNew);

            return null;
        }

        if (nodeReplacer != null) {
            nodeReplacer.replace(nodeOld);
        }

        return nodesQueue.getValue(nodeOld).setValue(value);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        return put(key, value, null);
    }

    public TValue putFirst(final TKey key, final TValue value) {
        return put(key, value, nodesQueue::offerFirst);
    }

    public TValue putLast(final TKey key, final TValue value) {
        return put(key, value, nodesQueue::offerLast);
    }

    @Override
    public TValue remove(final Object key) {
        Node nodeOld = nodesMap.remove(key);

        if (nodeOld == null) {
            return null;
        }

        nodesQueue.remove(nodeOld);

        return nodesQueue.getValue(nodeOld).getValue();
    }

    private TValue remove(final NodeRemover nodeRemover) {
        Node node = nodeRemover.remove();

        if (node == null) {
            return null;
        }

        Entry<TKey, TValue> entry = nodesQueue.getValue(node);

        nodesMap.remove(entry.getKey());

        return entry.getValue();
    }

    public TValue removeFirst() {
        return remove(nodesQueue::poll);
    }

    public TValue removeLast() {
        return remove(nodesQueue::pop);
    }

    @Override
    public void clear() {
        nodesMap.clear();
        nodesQueue.clear();
    }

    @Override
    protected Iterator<? extends Entry<TKey, TValue>> iterator() {
        return nodesQueue.stream()
                .map(nodesQueue::getValue)
                .iterator();
    }

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
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
    private interface NodeReplacer {
        boolean replace(Node node);
    }

    @FunctionalInterface
    private interface NodeRemover {
        Node remove();
    }
}
