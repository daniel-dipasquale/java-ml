package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.core.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.core.EnumValue;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.ai.rl.neat.internal.NoopRecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.internal.ProxyRecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.internal.RecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeOutputActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeStrategyActivationFunctionFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeGeneIdFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.factory.DualModeCyclicFloatFactory;
import com.dipasquale.synchronization.dual.mode.factory.DualModeFloatFactory;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DefaultContextNodeGeneSupport implements Context.NodeGeneSupport {
    private DualModeNodeGeneIdFactory nodeIdFactory;
    private Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories;
    private Map<NodeGeneType, RecurrentModifiersFactory> recurrentBiasesFactories;
    private Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories;
    private int inputs;
    private int outputs;
    private int biases;
    private List<Id> inputNodeIds;
    private List<Id> outputNodeIds;
    private List<Id> biasNodeIds;

    private DefaultContextNodeGeneSupport(final DualModeNodeGeneIdFactory nodeIdFactory, final Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories, final Map<NodeGeneType, RecurrentModifiersFactory> recurrentBiasesFactories, final Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
        this.nodeIdFactory = nodeIdFactory;
        this.biasFactories = biasFactories;
        this.recurrentBiasesFactories = recurrentBiasesFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.inputs = inputs;
        this.outputs = outputs;
        this.biases = biases;
        this.inputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.INPUT, inputs);
        this.outputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.OUTPUT, outputs);
        this.biasNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.BIAS, biases);
    }

    private static FloatNumber.DualModeFactory createBiasFactory(final InitializationContext initializationContext, final List<FloatNumber> biases) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return FloatNumber.createFactory(new DualModeFloatFactory(initializationContext.getConcurrencyLevel(), floatFactory));
        }

        List<FloatNumber.DualModeFactory> biasNodeBiasFactories = biases.stream()
                .map(bias -> bias.createFactory(initializationContext))
                .collect(Collectors.toList());

        return FloatNumber.createFactory(new DualModeCyclicFloatFactory<>(initializationContext.getConcurrencyLevel(), biasNodeBiasFactories));
    }

    private static Map<NodeGeneType, FloatNumber.DualModeFactory> createBiasFactories(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final NodeGeneSupport nodeGeneSupport) {
        Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories = new EnumMap<>(NodeGeneType.class);

        biasFactories.put(NodeGeneType.INPUT, nodeGeneSupport.getInputBias().createFactory(initializationContext));
        biasFactories.put(NodeGeneType.OUTPUT, nodeGeneSupport.getOutputBias().createFactory(initializationContext));
        biasFactories.put(NodeGeneType.BIAS, createBiasFactory(initializationContext, genesisGenomeTemplate.getBiases()));
        biasFactories.put(NodeGeneType.HIDDEN, nodeGeneSupport.getHiddenBias().createFactory(initializationContext));

        return biasFactories;
    }

    private static RecurrentModifiersFactory createRecurrentBiasesFactory(final InitializationContext initializationContext, final ConnectionGeneSupport connectionGeneSupport, final FloatNumber.DualModeFactory biasFactory) {
        float recurrentAllowanceRate = connectionGeneSupport.getRecurrentAllowanceRate().getSingletonValue(initializationContext);

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new NoopRecurrentModifiersFactory();
        }

        return new ProxyRecurrentModifiersFactory(biasFactory, connectionGeneSupport.getRecurrentStateType());
    }

    private static Map<NodeGeneType, RecurrentModifiersFactory> createRecurrentBiasesFactories(final InitializationContext initializationContext, final Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories, final ConnectionGeneSupport connectionGeneSupport) {
        Map<NodeGeneType, RecurrentModifiersFactory> recurrentBiasesFactories = new EnumMap<>(NodeGeneType.class);

        for (NodeGeneType type : biasFactories.keySet()) {
            FloatNumber.DualModeFactory biasFactory = biasFactories.get(type);
            RecurrentModifiersFactory recurrentBiasFactory = createRecurrentBiasesFactory(initializationContext, connectionGeneSupport, biasFactory);

            recurrentBiasesFactories.put(type, recurrentBiasFactory);
        }

        return recurrentBiasesFactories;
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final InitializationContext initializationContext, final EnumValue.DualModeFactory<ActivationFunctionType> activationFunctionTypeFactory) {
        DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeActivationFunctionFactory<>(initializationContext.createDefaultRandomSupport(), activationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeOutputActivationFunctionFactory<EnumValue.DualModeFactory<OutputActivationFunctionType>, EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final InitializationContext initializationContext, final EnumValue.DualModeFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory, final EnumValue.DualModeFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory) {
        DualModeOutputActivationFunctionFactory<EnumValue.DualModeFactory<OutputActivationFunctionType>, EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeOutputActivationFunctionFactory<>(initializationContext.createDefaultRandomSupport(), outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    private static DualModeStrategyActivationFunctionFactory<DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>>> createActivationFunctionFactory(final InitializationContext initializationContext, final ActivationFunctionType activationFunctionType) {
        EnumValue.DualModeFactory<ActivationFunctionType> activationFunctionTypeFactory = EnumValue.literal(activationFunctionType).createFactory(initializationContext);
        DualModeActivationFunctionFactory<EnumValue.DualModeFactory<ActivationFunctionType>> activationFunctionFactory = new DualModeActivationFunctionFactory<>(initializationContext.createDefaultRandomSupport(), activationFunctionTypeFactory);

        return new DualModeStrategyActivationFunctionFactory<>(activationFunctionFactory);
    }

    private static Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> createActivationFunctionFactories(final InitializationContext initializationContext, final NodeGeneSupport nodeGeneSupport) {
        Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories = new EnumMap<>(NodeGeneType.class);
        EnumValue.DualModeFactory<ActivationFunctionType> inputActivationFunctionTypeFactory = nodeGeneSupport.getInputActivationFunction().createFactory(initializationContext);
        EnumValue.DualModeFactory<OutputActivationFunctionType> outputActivationFunctionTypeFactory = nodeGeneSupport.getOutputActivationFunction().createFactory(initializationContext);
        EnumValue.DualModeFactory<ActivationFunctionType> hiddenActivationFunctionTypeFactory = nodeGeneSupport.getHiddenActivationFunction().createFactory(initializationContext);

        activationFunctionFactories.put(NodeGeneType.INPUT, createActivationFunctionFactory(initializationContext, inputActivationFunctionTypeFactory));
        activationFunctionFactories.put(NodeGeneType.OUTPUT, createActivationFunctionFactory(initializationContext, outputActivationFunctionTypeFactory, hiddenActivationFunctionTypeFactory));
        activationFunctionFactories.put(NodeGeneType.BIAS, createActivationFunctionFactory(initializationContext, ActivationFunctionType.IDENTITY));
        activationFunctionFactories.put(NodeGeneType.HIDDEN, createActivationFunctionFactory(initializationContext, hiddenActivationFunctionTypeFactory));

        return activationFunctionFactories;
    }

    public static DefaultContextNodeGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final NodeGeneSupport nodeGeneSupport, final ConnectionGeneSupport connectionGeneSupport) {
        DualModeNodeGeneIdFactory nodeIdFactory = new DualModeNodeGeneIdFactory(initializationContext.getConcurrencyLevel());
        Map<NodeGeneType, FloatNumber.DualModeFactory> biasFactories = createBiasFactories(initializationContext, genesisGenomeTemplate, nodeGeneSupport);
        Map<NodeGeneType, RecurrentModifiersFactory> recurrentBiasesFactories = createRecurrentBiasesFactories(initializationContext, biasFactories, connectionGeneSupport);
        Map<NodeGeneType, DualModeStrategyActivationFunctionFactory<?>> activationFunctionFactories = createActivationFunctionFactories(initializationContext, nodeGeneSupport);
        int inputs = genesisGenomeTemplate.getInputs().getSingletonValue(initializationContext);
        int outputs = genesisGenomeTemplate.getOutputs().getSingletonValue(initializationContext);
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultContextNodeGeneSupport(nodeIdFactory, biasFactories, recurrentBiasesFactories, activationFunctionFactories, inputs, outputs, biases);
    }

    private static List<Id> createNodeIds(final DualModeNodeGeneIdFactory nodeIdFactory, final NodeGeneType type, final int count) {
        return IntStream.range(0, count)
                .mapToObj(__ -> nodeIdFactory.createNodeId(type))
                .collect(Collectors.toList());
    }

    private NodeGene create(final Id id, final NodeGeneType type) {
        float bias = biasFactories.get(type).create();
        List<Float> recurrentBiases = recurrentBiasesFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).create();

        return new NodeGene(id, type, bias, recurrentBiases, activationFunction);
    }

    @Override
    public NodeGene createHidden() {
        Id id = nodeIdFactory.createNodeId(NodeGeneType.HIDDEN);

        return create(id, NodeGeneType.HIDDEN);
    }

    @Override
    public void setupInitialNodes(final Genome genome) {
        for (Id nodeId : inputNodeIds) {
            genome.getNodes().put(create(nodeId, NodeGeneType.INPUT));
        }

        for (Id nodeId : outputNodeIds) {
            genome.getNodes().put(create(nodeId, NodeGeneType.OUTPUT));
        }

        for (Id nodeId : biasNodeIds) {
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
        stateGroup.put("nodes.recurrentBiasesFactories", recurrentBiasesFactories);
        stateGroup.put("nodes.activationFunctionFactories", activationFunctionFactories);
        stateGroup.put("nodes.inputs", inputs);
        stateGroup.put("nodes.outputs", outputs);
        stateGroup.put("nodes.biases", biases);
        stateGroup.put("nodes.inputNodeIds", inputNodeIds);
        stateGroup.put("nodes.outputNodeIds", outputNodeIds);
        stateGroup.put("nodes.biasNodeIds", biasNodeIds);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        nodeIdFactory = DualModeObject.activateMode(stateGroup.get("nodes.nodeIdFactory"), concurrencyLevel);
        biasFactories = DualModeObject.forEachValueActivateMode(stateGroup.get("nodes.biasFactories"), concurrencyLevel);
        recurrentBiasesFactories = stateGroup.get("nodes.recurrentBiasesFactories");
        activationFunctionFactories = DualModeObject.forEachValueActivateMode(stateGroup.get("nodes.activationFunctionFactories"), concurrencyLevel);
        inputs = stateGroup.get("nodes.inputs");
        outputs = stateGroup.get("nodes.outputs");
        biases = stateGroup.get("nodes.biases");
        inputNodeIds = stateGroup.get("nodes.inputNodeIds");
        outputNodeIds = stateGroup.get("nodes.outputNodeIds");
        biasNodeIds = stateGroup.get("nodes.biasNodeIds");
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
