package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.common.Pair;
import com.dipasquale.data.structure.group.ElementKeyAccessor;
import com.dipasquale.data.structure.group.ListSetGroup;
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
    private final ListSetGroup<Id, NodeGene> nodeGenes = createNodeGenes();
    private final Map<NodeGeneType, ListSetGroup<Id, NodeGene>> nodeGenesByType = createNodeGenesByType();

    private static ListSetGroup<Id, NodeGene> createNodeGenes() {
        return new ListSetGroup<>((ElementKeyAccessor<Id, NodeGene> & Serializable) NodeGene::getId);
    }

    private static Map<NodeGeneType, ListSetGroup<Id, NodeGene>> createNodeGenesByType() {
        EnumMap<NodeGeneType, ListSetGroup<Id, NodeGene>> nodeGenesByType = new EnumMap<>(NodeGeneType.class);

        nodeGenesByType.put(NodeGeneType.INPUT, createNodeGenes());
        nodeGenesByType.put(NodeGeneType.OUTPUT, createNodeGenes());
        nodeGenesByType.put(NodeGeneType.BIAS, createNodeGenes());
        nodeGenesByType.put(NodeGeneType.HIDDEN, createNodeGenes());

        return nodeGenesByType;
    }

    public int size() {
        return nodeGenes.size();
    }

    public int size(final NodeGeneType type) {
        return nodeGenesByType.get(type).size();
    }

    public NodeGene getByIndex(final int index) {
        return nodeGenes.getByIndex(index);
    }

    public NodeGene getById(final Id id) {
        return nodeGenes.getById(id);
    }

    public NodeGene getRandom(final NeatContext.RandomnessSupport randomnessSupport) {
        return randomnessSupport.generateElement(nodeGenes);
    }

    public NodeGene getRandom(final NeatContext.RandomnessSupport randomnessSupport, final NodeGeneType type) {
        return randomnessSupport.generateElement(nodeGenesByType.get(type));
    }

    public void put(final NodeGene nodeGene) {
        nodeGenes.put(nodeGene);
        nodeGenesByType.get(nodeGene.getType()).put(nodeGene);
    }

    @Override
    public Iterator<NodeGene> iterator() {
        return nodeGenes.iterator();
    }

    public Iterator<NodeGene> iterator(final NodeGeneType type) {
        return nodeGenesByType.get(type).iterator();
    }

    public Iterator<Pair<NodeGene>> fullJoin(final NodeGeneGroup other) {
        return nodeGenes.fullJoin(other.nodeGenes);
    }
}
