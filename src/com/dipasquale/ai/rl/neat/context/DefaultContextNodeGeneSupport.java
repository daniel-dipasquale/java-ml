package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.DefaultActivationFunctionFactoryProfile;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.LiteralActivationFunctionFactoryProfile;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.OutputActivationFunctionFactoryProfile;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.EnumFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.IllegalStateFloatFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.factory.CyclicFloatFactoryProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DefaultContextNodeGeneSupport implements Context.NodeGeneSupport {
    private DualModeNodeIdFactory nodeIdFactory;
    private Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactories;
    private Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactories;
    private int inputs;
    private int outputs;
    private int biases;
    private List<SequentialId> inputNodeIds;
    private List<SequentialId> outputNodeIds;
    private List<SequentialId> biasNodeIds;

    private DefaultContextNodeGeneSupport(final DualModeNodeIdFactory nodeIdFactory, final Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactories, final Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
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

    private static ObjectProfile<FloatFactory> createBiasFactoryProfile(final ParallelismSupport parallelismSupport, final List<FloatNumber> biases) {
        if (biases.isEmpty()) {
            IllegalStateFloatFactory floatFactory = new IllegalStateFloatFactory("there are no biases allowed in this genome");

            return new DefaultObjectProfile<>(parallelismSupport.isEnabled(), floatFactory);
        }

        Iterable<Pair<FloatFactory>> biasNodeBiasFactoryPairs = biases.stream()
                .map(sfn -> ObjectProfile.deconstruct(sfn.createFactoryProfile(parallelismSupport)))
                ::iterator;

        return new CyclicFloatFactoryProfile(parallelismSupport.isEnabled(), biasNodeBiasFactoryPairs);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelismSupport, final ObjectProfile<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<ActivationFunctionType>> activationFunctionTypeFactoryPair = ObjectProfile.deconstruct(activationFunctionTypeFactoryProfile);

        return new DefaultActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), activationFunctionTypeFactoryPair);
    }

    private static ObjectProfile<ActivationFunctionFactory> createActivationFunctionFactoryProfile(final ParallelismSupport parallelismSupport, final ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile, final ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile) {
        Pair<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(outputActivationFunctionTypeFactoryProfile);
        Pair<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryPair = ObjectProfile.deconstruct(hiddenActivationFunctionTypeFactoryProfile);

        return new OutputActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), outputActivationFunctionTypeFactoryPair, hiddenActivationFunctionTypeFactoryPair);
    }

    public static DefaultContextNodeGeneSupport create(final ParallelismSupport parallelismSupport, final GenesisGenomeTemplate genesisGenomeTemplate, final NodeGeneSupport nodeGeneSupport) {
        DualModeNodeIdFactory nodeIdFactory = new DualModeNodeIdFactory(parallelismSupport.isEnabled());

        Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<FloatFactory>>builder()
                .put(NodeGeneType.INPUT, nodeGeneSupport.getInputBias().createFactoryProfile(parallelismSupport))
                .put(NodeGeneType.OUTPUT, nodeGeneSupport.getOutputBias().createFactoryProfile(parallelismSupport))
                .put(NodeGeneType.BIAS, createBiasFactoryProfile(parallelismSupport, genesisGenomeTemplate.getBiases()))
                .put(NodeGeneType.HIDDEN, nodeGeneSupport.getHiddenBias().createFactoryProfile(parallelismSupport))
                .build();

        ObjectProfile<EnumFactory<ActivationFunctionType>> inputActivationFunctionTypeFactoryProfile = nodeGeneSupport.getInputActivationFunction().createFactoryProfile(parallelismSupport);
        ObjectProfile<EnumFactory<OutputActivationFunctionType>> outputActivationFunctionTypeFactoryProfile = nodeGeneSupport.getOutputActivationFunction().createFactoryProfile(parallelismSupport);
        ObjectProfile<EnumFactory<ActivationFunctionType>> hiddenActivationFunctionTypeFactoryProfile = nodeGeneSupport.getHiddenActivationFunction().createFactoryProfile(parallelismSupport);

        Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactoryProfiles = ImmutableMap.<NodeGeneType, ObjectProfile<ActivationFunctionFactory>>builder()
                .put(NodeGeneType.INPUT, createActivationFunctionFactoryProfile(parallelismSupport, inputActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.OUTPUT, createActivationFunctionFactoryProfile(parallelismSupport, outputActivationFunctionTypeFactoryProfile, hiddenActivationFunctionTypeFactoryProfile))
                .put(NodeGeneType.BIAS, new LiteralActivationFunctionFactoryProfile(parallelismSupport.isEnabled(), ActivationFunctionType.IDENTITY))
                .put(NodeGeneType.HIDDEN, createActivationFunctionFactoryProfile(parallelismSupport, hiddenActivationFunctionTypeFactoryProfile))
                .build();

        int inputs = genesisGenomeTemplate.getInputs().createFactoryProfile(parallelismSupport).getObject().create();
        int outputs = genesisGenomeTemplate.getOutputs().createFactoryProfile(parallelismSupport).getObject().create();
        int biases = genesisGenomeTemplate.getBiases().size();

        return new DefaultContextNodeGeneSupport(nodeIdFactory, biasFactoryProfiles, activationFunctionFactoryProfiles, inputs, outputs, biases);
    }

    private static List<SequentialId> createNodeIds(final DualModeNodeIdFactory nodeIdFactory, final NodeGeneType type, final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> nodeIdFactory.createNodeId(type))
                .collect(Collectors.toList());
    }

    private NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).getObject().create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).getObject().create();

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
        nodeIdFactory = DualModeObject.switchMode(stateGroup.get("nodes.nodeIdFactory"), eventLoop != null);
        biasFactories = ObjectProfile.switchProfileMap(stateGroup.get("nodes.biasFactories"), eventLoop != null);
        activationFunctionFactories = ObjectProfile.switchProfileMap(stateGroup.get("nodes.activationFunctionFactories"), eventLoop != null);
        inputs = stateGroup.get("nodes.inputs");
        outputs = stateGroup.get("nodes.outputs");
        biases = stateGroup.get("nodes.biases");
        inputNodeIds = stateGroup.get("nodes.inputNodeIds");
        outputNodeIds = stateGroup.get("nodes.outputNodeIds");
        biasNodeIds = stateGroup.get("nodes.biasNodeIds");
    }
}
