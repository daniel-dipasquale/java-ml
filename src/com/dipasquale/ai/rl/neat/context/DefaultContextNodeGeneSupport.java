package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.settings.EnumValue;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeOutputActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeStrategyActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeGeneIdFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeCyclicFloatFactory;
import com.dipasquale.synchronization.dual.mode.factory.DualModeFloatFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DefaultContextNodeGeneSupport implements Context.NodeGeneSupport {
    private DualModeNodeGeneIdFactory nodeIdFactory;
    private Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories;
    private Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories;
    private int inputs;
    private int outputs;
    private int biases;
    private List<SequentialId> inputNodeIds;
    private List<SequentialId> outputNodeIds;
    private List<SequentialId> biasNodeIds;

    private DefaultContextNodeGeneSupport(final DualModeNodeGeneIdFactory nodeIdFactory, final Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories, final Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
        this.nodeIdFactory = nodeIdFactory;
        this.biasFactories = biasFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.inputs = inputs;
        this.outputs = outputs;
        this.biases = biases;
        this.inputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.INPUT, inputs);
        this.outputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.OUTPUT, outputs);
        this.biasNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.BIAS, biases);
    }

    private static FloatNumber.DualModeFactory createBiasFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final List<FloatNumber> biases) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return FloatNumber.createFactory(new DualModeFloatFactory(parallelismSupport.getConcurrencyLevel(), floatFactory));
        }

        List<FloatNumber.DualModeFactory> biasNodeBiasFactories = biases.stream()
                .map(sfn -> sfn.createFactory(parallelismSupport, randomSupports))
                .collect(Collectors.toList());

        return FloatNumber.createFactory(new DualModeCyclicFloatFactory<>(parallelismSupport.getConcurrencyLevel(), biasNodeBiasFactories));
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final DualModeRandomSupport randomSupport, final EnumValue.DualModeFactory<ActivationFunctionType> activationFunctionTypeFactory) {
        DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeActivationFunctionFactory<>(randomSupport, activationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeOutputActivationFunctionFactory<EnumValue.DualModeFactory<OutputActivationFunctionType>, EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final DualModeRandomSupport randomSupport, final EnumValue.DualModeFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumValue.DualModeFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        DualModeOutputActivationFunctionFactory<EnumValue.DualModeFactory<OutputActivationFunctionType>, EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeOutputActivationFunctionFactory<>(randomSupport, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final ActivationFunctionType activationFunctionType) {
        EnumValue.DualModeFactory<ActivationFunctionType> activationFunctionTypeFactory = EnumValue.literal(activationFunctionType).createFactory(parallelismSupport, randomSupports);
        DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeActivationFunctionFactory<>(randomSupport, activationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    public static DefaultContextNodeGeneSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final GenesisGenomeTemplate genesisGenomeTemplate, final NodeGeneSupport nodeGeneSupport) {
        DualModeNodeGeneIdFactory nodeIdFactory = new DualModeNodeGeneIdFactory(parallelismSupport.getConcurrencyLevel());

        Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories = ImmutableMap.<NodeGeneType, FloatNumber.DualModeFactory>builder()
                .put(NodeGeneType.INPUT, nodeGeneSupport.getInputBias().createFactory(parallelismSupport, randomSupports))
                .put(NodeGeneType.OUTPUT, nodeGeneSupport.getOutputBias().createFactory(parallelismSupport, randomSupports))
                .put(NodeGeneType.BIAS, createBiasFactory(parallelismSupport, randomSupports, genesisGenomeTemplate.getBiases()))
                .put(NodeGeneType.HIDDEN, nodeGeneSupport.getHiddenBias().createFactory(parallelismSupport, randomSupports))
                .build();

        EnumValue.DualModeFactory<ActivationFunctionType> inputActivationFunctionTypeFactory = nodeGeneSupport.getInputActivationFunction().createFactory(parallelismSupport, randomSupports);
        EnumValue.DualModeFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory = nodeGeneSupport.getOutputActivationFunction().createFactory(parallelismSupport, randomSupports);
        EnumValue.DualModeFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory = nodeGeneSupport.getHiddenActivationFunction().createFactory(parallelismSupport, randomSupports);

        Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories = ImmutableMap.<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactory(randomSupport, inputActivationFunctionTypeFactory))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactory(randomSupport, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory))
                .put(NodeGeneType.BIAS, createActivationFunctionFactory(parallelismSupport, randomSupports, randomSupport, ActivationFunctionType.IDENTITY))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactory(randomSupport, hiddenActivationFunctionTypeFactory))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().getSingletonValue(parallelismSupport, randomSupports);
        int outputs = genesisGenomeTemplate.getOutputs().getSingletonValue(parallelismSupport, randomSupports);
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultContextNodeGeneSupport(nodeIdFactory, biasFactories, activationFunctionFactories, inputs, outputs, biases);
    }

    private static List<SequentialId> createNodeIds(final DualModeNodeGeneIdFactory nodeIdFactory, final NodeGeneType type, final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> nodeIdFactory.createNodeId(type))
                .collect(Collectors.toList());
    }

    private NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).create();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public NodeGene createHidden() {
        SequentialId id = nodeIdFactory.createNodeId(NodeGeneType.HIDDEN);

        return create(id, NodeGeneType.HIDDEN);
    }

    @Override
    public void setupInitialNodes(final Genome genome) {
        for (SequentialId nodeId : inputNodeIds) {
            genome.getNodes().put(create(nodeId, NodeGeneType.INPUT));
        }

        for (SequentialId nodeId : outputNodeIds) {
            genome.getNodes().put(create(nodeId, NodeGeneType.OUTPUT));
        }

        for (SequentialId nodeId : biasNodeIds) {
            genome.getNodes().put(create(nodeId, NodeGeneType.BIAS));
        }
    }

    @Override
    public void reset() {
        nodeIdFactory.reset();
        inputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.INPUT, inputs);
        outputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.OUTPUT, outputs);
        biasNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.BIAS, biases);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("nodes.nodeIdFactory", nodeIdFactory);
        stateGroup.put("nodes.biasFactories", biasFactories);
        stateGroup.put("nodes.activationFunctionFactories", activationFunctionFactories);
        stateGroup.put("nodes.inputs", inputs);
        stateGroup.put("nodes.outputs", outputs);
        stateGroup.put("nodes.biases", biases);
        stateGroup.put("nodes.inputNodeIds", inputNodeIds);
        stateGroup.put("nodes.outputNodeIds", outputNodeIds);
        stateGroup.put("nodes.biasNodeIds", biasNodeIds);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        nodeIdFactory = DualModeObject.activateMode(stateGroup.get("nodes.nodeIdFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        biasFactories = DualModeObject.forEachValueActivateMode(stateGroup.get("nodes.biasFactories"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        activationFunctionFactories = DualModeObject.forEachValueActivateMode(stateGroup.get("nodes.activationFunctionFactories"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        inputs = stateGroup.get("nodes.inputs");
        outputs = stateGroup.get("nodes.outputs");
        biases = stateGroup.get("nodes.biases");
        inputNodeIds = stateGroup.get("nodes.inputNodeIds");
        outputNodeIds = stateGroup.get("nodes.outputNodeIds");
        biasNodeIds = stateGroup.get("nodes.biasNodeIds");
    }
}
