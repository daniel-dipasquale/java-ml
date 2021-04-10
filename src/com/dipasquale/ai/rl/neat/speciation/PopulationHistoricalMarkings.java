package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryDefault;
import com.dipasquale.ai.common.SequentialIdFactoryStrategy;
import com.dipasquale.ai.common.SequentialIdFactoryStrategySynchronized;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.google.common.collect.ImmutableMap;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PopulationHistoricalMarkings implements HistoricalMarkings {
    private final SequentialIdFactoryDefault genomeIdFactoryUnderlying;
    private SequentialIdFactory genomeIdFactory;
    private final SequentialIdFactoryDefault speciesIdFactoryUnderlying;
    private SequentialIdFactory speciesIdFactory;
    private final SequentialIdFactoryDefault innovationIdFactoryUnderlying;
    private SequentialIdFactory innovationIdFactory;
    private Map<DirectedEdge, InnovationId> innovationIds;
    private final Map<NodeGeneType, SequentialIdFactoryDefault> nodeIdFactoriesUnderlying;
    private Map<NodeGeneType, SequentialIdFactory> nodeIdFactories;
    private List<SequentialId> inputNodesIds;
    private List<SequentialId> outputNodeIds;
    private List<SequentialId> biasNodeIds;
    private Deque<String> genomeIdsKilled;

    PopulationHistoricalMarkings() {
        this.genomeIdFactoryUnderlying = new SequentialIdFactoryDefault();
        this.genomeIdFactory = null;
        this.speciesIdFactoryUnderlying = new SequentialIdFactoryDefault();
        this.speciesIdFactory = null;
        this.innovationIdFactoryUnderlying = new SequentialIdFactoryDefault();
        this.innovationIdFactory = null;
        this.innovationIds = null;
        this.nodeIdFactoriesUnderlying = createSequentialIdFactories();
        this.nodeIdFactories = null;
        this.inputNodesIds = null;
        this.outputNodeIds = null;
        this.biasNodeIds = null;
        this.genomeIdsKilled = null;
    }

    private boolean isInitialized() {
        return inputNodesIds != null;
    }

    private static SequentialIdFactory createSequentialIdFactory(final boolean parallel, final String name, final SequentialIdFactory sequentialIdFactory) {
        if (!parallel) {
            return new SequentialIdFactoryStrategy(name, sequentialIdFactory);
        }

        return new SequentialIdFactoryStrategySynchronized(name, sequentialIdFactory);
    }

    private SequentialIdFactory createGenomeIdFactory(final Context context) {
        return createSequentialIdFactory(context.parallelism().isEnabled(), "genome", genomeIdFactoryUnderlying);
    }

    private SequentialIdFactory createSpeciesIdFactory(final Context context) {
        return createSequentialIdFactory(context.parallelism().isEnabled(), "species", speciesIdFactoryUnderlying);
    }

    private SequentialIdFactory createInnovationIdFactory(final Context context) {
        return createSequentialIdFactory(context.parallelism().isEnabled(), "innovation-id", innovationIdFactoryUnderlying);
    }

    private Map<DirectedEdge, InnovationId> createInnovationIds(final Context context) {
        Map<DirectedEdge, InnovationId> innovationIdsReplacement = !context.parallelism().isEnabled()
                ? new HashMap<>()
                : new ConcurrentHashMap<>(16, 0.75f, context.parallelism().numberOfThreads());

        if (innovationIds != null) {
            innovationIdsReplacement.putAll(innovationIds);
        }

        return innovationIdsReplacement;
    }

    private static Map<NodeGeneType, SequentialIdFactoryDefault> createSequentialIdFactories() {
        return ImmutableMap.<NodeGeneType, SequentialIdFactoryDefault>builder()
                .put(NodeGeneType.INPUT, new SequentialIdFactoryDefault())
                .put(NodeGeneType.OUTPUT, new SequentialIdFactoryDefault())
                .put(NodeGeneType.BIAS, new SequentialIdFactoryDefault())
                .put(NodeGeneType.HIDDEN, new SequentialIdFactoryDefault())
                .build();
    }

    private Map<NodeGeneType, SequentialIdFactory> createNodeIdFactories(final Context context) {
        boolean parallel = context.parallelism().isEnabled();

        return ImmutableMap.<NodeGeneType, SequentialIdFactory>builder()
                .put(NodeGeneType.INPUT, createSequentialIdFactory(parallel, "n1-input", nodeIdFactoriesUnderlying.get(NodeGeneType.INPUT)))
                .put(NodeGeneType.OUTPUT, createSequentialIdFactory(parallel, "n4-output", nodeIdFactoriesUnderlying.get(NodeGeneType.OUTPUT)))
                .put(NodeGeneType.BIAS, createSequentialIdFactory(parallel, "n2-bias", nodeIdFactoriesUnderlying.get(NodeGeneType.BIAS)))
                .put(NodeGeneType.HIDDEN, createSequentialIdFactory(parallel, "n3-hidden", nodeIdFactoriesUnderlying.get(NodeGeneType.HIDDEN)))
                .build();
    }

    private List<SequentialId> createNodeIds(final NodeGeneType type, final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createNodeId(type))
                .collect(Collectors.toList());
    }

    private Deque<String> replaceGenomeIdsKilled(final Context context) {
        Deque<String> genomeIdsKilledReplacement = !context.parallelism().isEnabled()
                ? new LinkedList<>()
                : new ConcurrentLinkedDeque<>();

        if (genomeIdsKilled != null) {
            genomeIdsKilledReplacement.addAll(genomeIdsKilled);
        }

        return genomeIdsKilledReplacement;
    }

    public void initialize(final Context context) {
        if (isInitialized() && (inputNodesIds.size() != context.nodes().size(NodeGeneType.INPUT) || outputNodeIds.size() != context.nodes().size(NodeGeneType.OUTPUT) || biasNodeIds.size() != context.nodes().size(NodeGeneType.BIAS))) {
            throw new IllegalStateException("unable to change the number of input, output or bia nodes after initialization ... yet!");
        }

        genomeIdFactory = createGenomeIdFactory(context);
        speciesIdFactory = createSpeciesIdFactory(context);
        innovationIdFactory = createInnovationIdFactory(context);
        innovationIds = createInnovationIds(context);
        nodeIdFactories = createNodeIdFactories(context);
        inputNodesIds = createNodeIds(NodeGeneType.INPUT, context.nodes().size(NodeGeneType.INPUT));
        outputNodeIds = createNodeIds(NodeGeneType.OUTPUT, context.nodes().size(NodeGeneType.OUTPUT));
        biasNodeIds = createNodeIds(NodeGeneType.BIAS, context.nodes().size(NodeGeneType.BIAS));
        genomeIdsKilled = replaceGenomeIdsKilled(context);
    }

    @Override
    public String createGenomeId() {
        String id = genomeIdsKilled.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    public GenomeDefault createGenome(final Context context) {
        GenomeDefault genome = new GenomeDefault(createGenomeId(), this);

        for (SequentialId nodeId : inputNodesIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.INPUT));
        }

        for (SequentialId nodeId : outputNodeIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.OUTPUT));
        }

        for (SequentialId nodeId : biasNodeIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.BIAS));
        }

        context.connections().setupInitialConnection(genome, this);

        return genome;
    }

    public String createSpecies() {
        return speciesIdFactory.create().toString();
    }

    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId(de, innovationIdFactory.create()));
    }

    public InnovationId getOrCreateInnovationId(final SequentialId inputNodeId, final SequentialId outputNodeId) {
        DirectedEdge directedEdge = new DirectedEdge(inputNodeId, outputNodeId);

        return getOrCreateInnovationId(directedEdge);
    }

    @Override
    public InnovationId getOrCreateInnovationId(final NodeGene inputNode, final NodeGene outputNode) {
        return getOrCreateInnovationId(inputNode.getId(), outputNode.getId());
    }

    @Override
    public SequentialId createNodeId(final NodeGeneType type) {
        return nodeIdFactories.get(type).create();
    }

    public void markToKill(final GenomeDefault genome) {
        genomeIdsKilled.add(genome.getId());
    }

    public int getGenomeKilledCount() {
        return genomeIdsKilled.size();
    }

    public void reset(final Context.NodeGeneSupport nodes) {
        genomeIdFactory.reset();
        speciesIdFactory.reset();
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeIdFactories.values().forEach(SequentialIdFactory::reset);
        inputNodesIds = createNodeIds(NodeGeneType.INPUT, nodes.size(NodeGeneType.INPUT));
        outputNodeIds = createNodeIds(NodeGeneType.OUTPUT, nodes.size(NodeGeneType.OUTPUT));
        biasNodeIds = createNodeIds(NodeGeneType.BIAS, nodes.size(NodeGeneType.BIAS));
        genomeIdsKilled.clear();
    }
}
