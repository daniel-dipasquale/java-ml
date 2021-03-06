package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneMap<T extends Comparable<T>> implements Iterable<NodeGene<T>> {
    private final Context<T> context;
    private final SequentialMap<T, NodeGene<T>> nodes = new SequentialMap<>();
    private final Map<NodeGeneType, SequentialMap<T, NodeGene<T>>> nodesByType = createNodesByType();

    private static <T extends Comparable<T>> Map<NodeGeneType, SequentialMap<T, NodeGene<T>>> createNodesByType() {
        EnumMap<NodeGeneType, SequentialMap<T, NodeGene<T>>> nodesByType = new EnumMap<>(NodeGeneType.class);

        nodesByType.put(NodeGeneType.Input, new SequentialMap<>());
        nodesByType.put(NodeGeneType.Output, new SequentialMap<>());
        nodesByType.put(NodeGeneType.Bias, new SequentialMap<>());
        nodesByType.put(NodeGeneType.Hidden, new SequentialMap<>());

        return nodesByType;
    }

    public int size() {
        return nodes.size();
    }

    public int size(final NodeGeneType type) {
        return nodesByType.get(type).size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public NodeGene<T> getByIndex(final int index) {
        return nodes.getByIndex(index);
    }

    public NodeGene<T> getByIndex(final NodeGeneType type, final int index) {
        return nodesByType.get(type).getByIndex(index);
    }

    public NodeGene<T> getById(final T id) {
        return nodes.getById(id);
    }

    public NodeGene<T> getRandom() {
        return context.random().nextItem(nodes);
    }

    public NodeGene<T> getRandom(final NodeGeneType type) {
        return context.random().nextItem(nodesByType.get(type));
    }

    public void put(final NodeGene<T> node) {
        nodes.put(node.getId(), node);
        nodesByType.get(node.getType()).put(node.getId(), node);
    }

    public Iterable<SequentialMap<T, NodeGene<T>>.JoinEntry> fullJoinFromAll(final NodeGeneMap<T> other) {
        return nodes.fullJoin(other.nodes);
    }

    @Override
    public Iterator<NodeGene<T>> iterator() {
        return nodes.iterator();
    }

    public Iterator<NodeGene<T>> iterator(final NodeGeneType type) {
        return nodesByType.get(type).iterator();
    }
}
