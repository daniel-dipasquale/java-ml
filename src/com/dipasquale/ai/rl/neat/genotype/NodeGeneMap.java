/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialMap;
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
public final class NodeGeneMap implements Iterable<NodeGene>, Serializable {
    @Serial
    private static final long serialVersionUID = -414060625780283286L;
    @EqualsAndHashCode.Include
    private final SequentialMap<SequentialId, NodeGene> nodes = new SequentialMap<>();
    private final Map<NodeGeneType, SequentialMap<SequentialId, NodeGene>> nodesByType = createNodesByType();

    private static Map<NodeGeneType, SequentialMap<SequentialId, NodeGene>> createNodesByType() {
        EnumMap<NodeGeneType, SequentialMap<SequentialId, NodeGene>> nodesByType = new EnumMap<>(NodeGeneType.class);

        nodesByType.put(NodeGeneType.INPUT, new SequentialMap<>());
        nodesByType.put(NodeGeneType.OUTPUT, new SequentialMap<>());
        nodesByType.put(NodeGeneType.BIAS, new SequentialMap<>());
        nodesByType.put(NodeGeneType.HIDDEN, new SequentialMap<>());

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

    public Iterable<Pair<NodeGene>> fullJoin(final NodeGeneMap other) {
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
