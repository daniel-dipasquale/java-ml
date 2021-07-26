package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.common.sequence.DefaultSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.ai.common.sequence.StrategySequentialIdFactory;
import com.dipasquale.ai.common.sequence.SynchronizedStrategySequentialIdFactory;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenomeHistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class PopulationHistoricalMarkings implements GenomeHistoricalMarkings, Serializable {
    @Serial
    private static final long serialVersionUID = -3580686562257388659L;
    private boolean initialized = false;
    private final DefaultSequentialIdFactory genomeIdFactoryUnderlying = new DefaultSequentialIdFactory();
    private SequentialIdFactory genomeIdFactory = null;
    private final DefaultSequentialIdFactory speciesIdFactoryUnderlying = new DefaultSequentialIdFactory();
    private SequentialIdFactory speciesIdFactory = null;
    private final DefaultSequentialIdFactory innovationIdFactoryUnderlying = new DefaultSequentialIdFactory();
    private SequentialIdFactory innovationIdFactory = null;
    private Map<DirectedEdge, InnovationId> innovationIds = null;
    private final Map<NodeGeneType, DefaultSequentialIdFactory> nodeIdFactoriesUnderlying = createSequentialIdFactories();
    private Map<NodeGeneType, SequentialIdFactory> nodeIdFactories = null;
    private List<SequentialId> inputNodesIds = null;
    private List<SequentialId> outputNodeIds = null;
    private List<SequentialId> biasNodeIds = null;
    private Deque<String> genomeIdsKilled = null;

    private static SequentialIdFactory createSequentialIdFactory(final boolean parallel, final String name, final SequentialIdFactory sequentialIdFactory) {
        if (!parallel) {
            return new StrategySequentialIdFactory(name, sequentialIdFactory);
        }

        return new SynchronizedStrategySequentialIdFactory(name, sequentialIdFactory);
    }

    private SequentialIdFactory createGenomeIdFactory(final Context.ParallelismSupport parallelism) {
        return createSequentialIdFactory(parallelism.isEnabled(), "genome", genomeIdFactoryUnderlying);
    }

    private SequentialIdFactory createSpeciesIdFactory(final Context.ParallelismSupport parallelism) {
        return createSequentialIdFactory(parallelism.isEnabled(), "species", speciesIdFactoryUnderlying);
    }

    private SequentialIdFactory createInnovationIdFactory(final Context.ParallelismSupport parallelism) {
        return createSequentialIdFactory(parallelism.isEnabled(), "innovation-id", innovationIdFactoryUnderlying);
    }

    private Map<DirectedEdge, InnovationId> createInnovationIds(final Context.ParallelismSupport parallelism) {
        Map<DirectedEdge, InnovationId> innovationIdsReplacement = !parallelism.isEnabled()
                ? new HashMap<>()
                : new ConcurrentHashMap<>(16, 0.75f, parallelism.numberOfThreads());

        if (innovationIds != null) {
            innovationIdsReplacement.putAll(innovationIds);
        }

        return innovationIdsReplacement;
    }

    private static Map<NodeGeneType, DefaultSequentialIdFactory> createSequentialIdFactories() {
        return ImmutableMap.<NodeGeneType, DefaultSequentialIdFactory>builder()
                .put(NodeGeneType.INPUT, new DefaultSequentialIdFactory())
                .put(NodeGeneType.OUTPUT, new DefaultSequentialIdFactory())
                .put(NodeGeneType.BIAS, new DefaultSequentialIdFactory())
                .put(NodeGeneType.HIDDEN, new DefaultSequentialIdFactory())
                .build();
    }

    private Map<NodeGeneType, SequentialIdFactory> createNodeIdFactories(final Context.ParallelismSupport parallelism) {
        boolean parallel = parallelism.isEnabled();

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

    private Deque<String> replaceGenomeIdsKilled(final Context.ParallelismSupport parallelism) {
        Deque<String> genomeIdsKilledReplacement = !parallelism.isEnabled()
                ? new LinkedList<>()
                : new ConcurrentLinkedDeque<>();

        if (genomeIdsKilled != null) {
            genomeIdsKilledReplacement.addAll(genomeIdsKilled);
        }

        return genomeIdsKilledReplacement;
    }

    public void initialize(final Context context) {
        if (initialized && (inputNodesIds.size() != context.nodes().size(NodeGeneType.INPUT) || outputNodeIds.size() != context.nodes().size(NodeGeneType.OUTPUT) || biasNodeIds.size() != context.nodes().size(NodeGeneType.BIAS))) {
            throw new IllegalStateException("unable to change the number of input, output or bia nodes after initialization ... yet!");
        }

        initialized = true;
        genomeIdFactory = createGenomeIdFactory(context.parallelism());
        speciesIdFactory = createSpeciesIdFactory(context.parallelism());
        innovationIdFactory = createInnovationIdFactory(context.parallelism());
        innovationIds = createInnovationIds(context.parallelism());
        nodeIdFactories = createNodeIdFactories(context.parallelism());
        inputNodesIds = createNodeIds(NodeGeneType.INPUT, context.nodes().size(NodeGeneType.INPUT));
        outputNodeIds = createNodeIds(NodeGeneType.OUTPUT, context.nodes().size(NodeGeneType.OUTPUT));
        biasNodeIds = createNodeIds(NodeGeneType.BIAS, context.nodes().size(NodeGeneType.BIAS));
        genomeIdsKilled = replaceGenomeIdsKilled(context.parallelism());
    }

    @Override
    public String createGenomeId() {
        String id = genomeIdsKilled.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    public DefaultGenome createGenome(final Context context) {
        DefaultGenome genome = new DefaultGenome(createGenomeId(), this);

        for (SequentialId nodeId : inputNodesIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.INPUT));
        }

        for (SequentialId nodeId : outputNodeIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.OUTPUT));
        }

        for (SequentialId nodeId : biasNodeIds) {
            genome.addNode(context.nodes().create(nodeId, NodeGeneType.BIAS));
        }

        context.connections().setupInitialConnections(genome, this);

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

    public void markToKill(final DefaultGenome genome) {
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
