package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneMap implements Iterable<NodeGene> {
    private final Context context;
    private final SequentialMap<SequentialId, NodeGene> nodes = new SequentialMap<>();
    private final Map<NodeGeneType, SequentialMap<SequentialId, NodeGene>> nodesByType = createNodesByType();

    private static Map<NodeGeneType, SequentialMap<SequentialId, NodeGene>> createNodesByType() {
        EnumMap<NodeGeneType, SequentialMap<SequentialId, NodeGene>> nodesByType = new EnumMap<>(NodeGeneType.class);

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

    public NodeGene getByIndex(final int index) {
        return nodes.getByIndex(index);
    }

    public NodeGene getByIndex(final NodeGeneType type, final int index) {
        return nodesByType.get(type).getByIndex(index);
    }

    public NodeGene getById(final SequentialId id) {
        return nodes.getById(id);
    }

    public NodeGene getRandom() {
        return context.random().nextItem(nodes);
    }

    public NodeGene getRandom(final NodeGeneType type) {
        return context.random().nextItem(nodesByType.get(type));
    }

    public void put(final NodeGene node) {
        nodes.put(node.getId(), node);
        nodesByType.get(node.getType()).put(node.getId(), node);
    }

    public Iterable<SequentialMap<SequentialId, NodeGene>.JoinEntry> fullJoinFromAll(final NodeGeneMap other) {
        return nodes.fullJoin(other.nodes);
    }

    @Override
    public Iterator<NodeGene> iterator() {
        return nodes.iterator();
    }

    public Iterator<NodeGene> iterator(final NodeGeneType type) {
        return nodesByType.get(type).iterator();
    }
}
