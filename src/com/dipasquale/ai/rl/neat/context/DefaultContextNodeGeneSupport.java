package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeIdFactory;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

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

    public DefaultContextNodeGeneSupport(final DualModeNodeIdFactory nodeIdFactory, final Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactories, final Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
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

    public void save(final SerializableStateGroup state) {
        state.put("nodes.nodeIdFactory", nodeIdFactory);
        state.put("nodes.biasFactories", biasFactories);
        state.put("nodes.activationFunctionFactories", activationFunctionFactories);
        state.put("nodes.inputs", inputs);
        state.put("nodes.outputs", outputs);
        state.put("nodes.biases", biases);
        state.put("nodes.inputNodeIds", inputNodeIds);
        state.put("nodes.outputNodeIds", outputNodeIds);
        state.put("nodes.biasNodeIds", biasNodeIds);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        nodeIdFactory = DualModeObject.switchMode(state.get("nodes.nodeIdFactory"), eventLoop != null);
        biasFactories = ObjectProfile.switchProfileMap(state.get("nodes.biasFactories"), eventLoop != null);
        activationFunctionFactories = ObjectProfile.switchProfileMap(state.get("nodes.activationFunctionFactories"), eventLoop != null);
        inputs = state.get("nodes.inputs");
        outputs = state.get("nodes.outputs");
        biases = state.get("nodes.biases");
        inputNodeIds = state.get("nodes.inputNodeIds");
        outputNodeIds = state.get("nodes.outputNodeIds");
        biasNodeIds = state.get("nodes.biasNodeIds");
    }
}
