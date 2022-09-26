package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.RecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.StrategyActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneGroup;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneIdFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class DefaultNeatContextNodeGeneSupport implements NeatContext.NodeGeneSupport {
    private final NodeGeneIdFactory nodeGeneIdFactory;
    private final Map<NodeGeneType, FloatFactory> biasFactories;
    private final Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories;
    private final Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories;
    private final NodeGeneTemplateParams nodeGeneTemplateParams;
    private final NodeGeneTemplate nodeGeneTemplate;
    private final HistoricalMarkings historicalMarkings;

    private static List<Id> createNodeGeneIds(final int count, final NodeGeneIdFactory nodeIdFactory, final NodeGeneType type) {
        return IntStream.range(0, count)
                .mapToObj(__ -> nodeIdFactory.create(type))
                .collect(Collectors.toList());
    }

    private static NodeGene createNodeGene(final Id id, final NodeGeneType type, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories, final Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories) {
        float bias = biasFactories.get(type).create();
        List<Float> recurrentBiases = recurrentBiasesFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).create();

        return new NodeGene(id, type, bias, recurrentBiases, activationFunction);
    }

    private static List<NodeGene> createHiddenNodeGenes(final int hiddenCount, final NodeGeneIdFactory nodeGeneIdFactory, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories, final Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories) {
        return IntStream.range(0, hiddenCount)
                .mapToObj(index -> createNodeGene(nodeGeneIdFactory.create(NodeGeneType.HIDDEN), NodeGeneType.HIDDEN, biasFactories, recurrentBiasesFactories, activationFunctionFactories))
                .collect(Collectors.toList());
    }

    DefaultNeatContextNodeGeneSupport(final NodeGeneIdFactory nodeGeneIdFactory, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories, final Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories, final NodeGeneTemplateParams nodeGeneTemplateParams, final HistoricalMarkings historicalMarkings) {
        NodeGeneTemplate nodeGeneTemplate = NodeGeneTemplate.builder()
                .inputNodeGeneIds(createNodeGeneIds(nodeGeneTemplateParams.inputCount, nodeGeneIdFactory, NodeGeneType.INPUT))
                .outputNodeGeneIds(createNodeGeneIds(nodeGeneTemplateParams.outputCount, nodeGeneIdFactory, NodeGeneType.OUTPUT))
                .biasNodeGeneIds(createNodeGeneIds(nodeGeneTemplateParams.biasCount, nodeGeneIdFactory, NodeGeneType.BIAS))
                .hiddenNodeGenes(createHiddenNodeGenes(nodeGeneTemplateParams.hiddenCount, nodeGeneIdFactory, biasFactories, recurrentBiasesFactories, activationFunctionFactories))
                .build();

        this.nodeGeneIdFactory = nodeGeneIdFactory;
        this.biasFactories = biasFactories;
        this.recurrentBiasesFactories = recurrentBiasesFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.nodeGeneTemplateParams = nodeGeneTemplateParams;
        this.nodeGeneTemplate = nodeGeneTemplate;
        this.historicalMarkings = historicalMarkings;
    }

    private NodeGene createNodeGene(final Id id, final NodeGeneType type) {
        return createNodeGene(id, type, biasFactories, recurrentBiasesFactories, activationFunctionFactories);
    }

    @Override
    public NodeGene createHidden() {
        Id id = nodeGeneIdFactory.create(NodeGeneType.HIDDEN);

        return createNodeGene(id, NodeGeneType.HIDDEN);
    }

    @Override
    public void setupInitial(final Genome genome) {
        NodeGeneGroup nodeGenes = genome.getNodeGenes();

        for (Id inputNodeGeneId : nodeGeneTemplate.inputNodeGeneIds) {
            NodeGene inputNodeGene = createNodeGene(inputNodeGeneId, NodeGeneType.INPUT);

            nodeGenes.put(inputNodeGene);
        }

        for (Id outputNodeGeneId : nodeGeneTemplate.outputNodeGeneIds) {
            NodeGene outputNodeGene = createNodeGene(outputNodeGeneId, NodeGeneType.OUTPUT);

            nodeGenes.put(outputNodeGene);
        }

        for (Id biasNodeGeneId : nodeGeneTemplate.biasNodeGeneIds) {
            NodeGene biasNodeGene = createNodeGene(biasNodeGeneId, NodeGeneType.BIAS);

            nodeGenes.put(biasNodeGene);
        }

        for (NodeGene hiddenNodeGene : nodeGeneTemplate.hiddenNodeGenes) {
            nodeGenes.put(hiddenNodeGene);
        }
    }

    private static Iterable<NodeGene> getHiddenNodeGenes(final Genome genome) {
        return () -> genome.getNodeGenes().iterator(NodeGeneType.HIDDEN);
    }

    @Override
    public void registerAll(final Genome genome) {
        for (NodeGene nodeGene : getHiddenNodeGenes(genome)) {
            historicalMarkings.registerNodeGene(nodeGene);
        }
    }

    @Override
    public void deregisterAll(final Genome genome) {
        for (NodeGene nodeGene : getHiddenNodeGenes(genome)) {
            historicalMarkings.deregisterNodeGene(nodeGene);
        }
    }

    @Override
    public void clear() {
        nodeGeneIdFactory.reset();
        nodeGeneTemplate.reset(this);
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("nodes.nodeGeneIdFactory", nodeGeneIdFactory);
        stateGroup.put("nodes.biasFactories", biasFactories);
        stateGroup.put("nodes.recurrentBiasesFactories", recurrentBiasesFactories);
        stateGroup.put("nodes.activationFunctionFactories", activationFunctionFactories);
        stateGroup.put("nodes.nodeGeneTemplateParams", nodeGeneTemplateParams);
        stateGroup.put("nodes.nodeGeneTemplate", nodeGeneTemplate);
        stateGroup.put("nodes.historicalMarkings", historicalMarkings);
    }

    static DefaultNeatContextNodeGeneSupport create(final SerializableStateGroup stateGroup) {
        NodeGeneIdFactory nodeGeneIdFactory = stateGroup.get("nodes.nodeGeneIdFactory");
        Map<NodeGeneType, FloatFactory> biasFactories = stateGroup.get("nodes.biasFactories");
        Map<NodeGeneType, RecurrentWeightFactory> recurrentBiasesFactories = stateGroup.get("nodes.recurrentBiasesFactories");
        Map<NodeGeneType, StrategyActivationFunctionFactory> activationFunctionFactories = stateGroup.get("nodes.activationFunctionFactories");
        NodeGeneTemplateParams nodeGeneTemplateParams = stateGroup.get("nodes.nodeGeneTemplateParams");
        NodeGeneTemplate nodeGeneTemplate = stateGroup.get("nodes.nodeGeneTemplate");
        HistoricalMarkings historicalMarkings = stateGroup.get("nodes.historicalMarkings");

        return new DefaultNeatContextNodeGeneSupport(nodeGeneIdFactory, biasFactories, recurrentBiasesFactories, activationFunctionFactories, nodeGeneTemplateParams, nodeGeneTemplate, historicalMarkings);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PACKAGE)
    static final class NodeGeneTemplateParams implements Serializable {
        @Serial
        private static final long serialVersionUID = 7897882169753441791L;
        private final int inputCount;
        private final int outputCount;
        private final int biasCount;
        private final int hiddenCount;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NodeGeneTemplate implements Serializable {
        @Serial
        private static final long serialVersionUID = -6320922664819469990L;
        private final List<Id> inputNodeGeneIds;
        private final List<Id> outputNodeGeneIds;
        private final List<Id> biasNodeGeneIds;
        private final List<NodeGene> hiddenNodeGenes;

        private void reset(final DefaultNeatContextNodeGeneSupport support) {
            inputNodeGeneIds.clear();
            inputNodeGeneIds.addAll(createNodeGeneIds(support.nodeGeneTemplateParams.inputCount, support.nodeGeneIdFactory, NodeGeneType.INPUT));
            outputNodeGeneIds.clear();
            outputNodeGeneIds.addAll(createNodeGeneIds(support.nodeGeneTemplateParams.outputCount, support.nodeGeneIdFactory, NodeGeneType.OUTPUT));
            biasNodeGeneIds.clear();
            biasNodeGeneIds.addAll(createNodeGeneIds(support.nodeGeneTemplateParams.biasCount, support.nodeGeneIdFactory, NodeGeneType.BIAS));
            hiddenNodeGenes.clear();
            hiddenNodeGenes.addAll(createHiddenNodeGenes(support.nodeGeneTemplateParams.hiddenCount, support.nodeGeneIdFactory, support.biasFactories, support.recurrentBiasesFactories, support.activationFunctionFactories));
        }
    }
}
