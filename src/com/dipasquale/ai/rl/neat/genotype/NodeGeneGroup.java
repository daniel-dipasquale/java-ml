package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.OrderedGroup;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class NodeGeneGroup implements Iterable<NodeGene>, Serializable {
    @Serial
    private static final long serialVersionUID = -414060625780283286L;
    @EqualsAndHashCode.Include
    private final OrderedGroup<SequentialId, NodeGene> nodes = new OrderedGroup<>();
    private final Map<NodeGeneType, OrderedGroup<SequentialId, NodeGene>> nodesByType = createNodesByType();

    private static Map<NodeGeneType, OrderedGroup<SequentialId, NodeGene>> createNodesByType() {
        EnumMap<NodeGeneType, OrderedGroup<SequentialId, NodeGene>> nodesByType = new EnumMap<>(NodeGeneType.class);

        nodesByType.put(NodeGeneType.INPUT, new OrderedGroup<>());
        nodesByType.put(NodeGeneType.OUTPUT, new OrderedGroup<>());
        nodesByType.put(NodeGeneType.BIAS, new OrderedGroup<>());
        nodesByType.put(NodeGeneType.HIDDEN, new OrderedGroup<>());

        return nodesByType;
    }

    public int size() {
        return nodes.size();
    }

    public int size(final NodeGeneType type) {
        return nodesByType.get(type).size();
    }

    public NodeGene getByIndex(final int index) {
        return nodes.getByIndex(index);
    }

    public NodeGene getById(final SequentialId id) {
        return nodes.getById(id);
    }

    public NodeGene getRandom(final Context.RandomSupport random) {
        return random.nextItem(nodes);
    }

    public NodeGene getRandom(final Context.RandomSupport random, final NodeGeneType type) {
        return random.nextItem(nodesByType.get(type));
    }

    public void put(final NodeGene node) {
        nodes.put(node.getId(), node);
        nodesByType.get(node.getType()).put(node.getId(), node);
    }

    @Override
    public Iterator<NodeGene> iterator() {
        return nodes.iterator();
    }

    public Iterator<NodeGene> iterator(final NodeGeneType type) {
        return nodesByType.get(type).iterator();
    }

    public Iterator<Pair<NodeGene>> fullJoin(final NodeGeneGroup other) {
        return nodes.fullJoin(other.nodes);
    }
}
