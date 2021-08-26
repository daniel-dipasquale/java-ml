package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeNodeIdFactory;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.FloatFactory;
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
    private List<SequentialId> inputNodeIds;
    private List<SequentialId> outputNodeIds;
    private List<SequentialId> biasNodeIds;

    public DefaultContextNodeGeneSupport(final DualModeNodeIdFactory nodeIdFactory, final Map<NodeGeneType, ObjectProfile<FloatFactory>> biasFactories, final Map<NodeGeneType, ObjectProfile<ActivationFunctionFactory>> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
        this.nodeIdFactory = nodeIdFactory;
        this.biasFactories = biasFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.inputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.INPUT, inputs);
        this.outputNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.OUTPUT, outputs);
        this.biasNodeIds = createNodeIds(nodeIdFactory, NodeGeneType.BIAS, biases);
    }

    private static List<SequentialId> createNodeIds(final DualModeNodeIdFactory nodeIdFactory, final NodeGeneType type, final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> nodeIdFactory.createNodeId(type))
                .collect(Collectors.toList());
    }

    @Override
    public SequentialId createId(final NodeGeneType type) {
        return nodeIdFactory.createNodeId(type);
    }

    @Override
    public NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).getObject().create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).getObject().create();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public void setupInitialNodes(final DefaultGenome genome) {
        for (SequentialId nodeId : inputNodeIds) {
            genome.addNode(create(nodeId, NodeGeneType.INPUT));
        }

        for (SequentialId nodeId : outputNodeIds) {
            genome.addNode(create(nodeId, NodeGeneType.OUTPUT));
        }

        for (SequentialId nodeId : biasNodeIds) {
            genome.addNode(create(nodeId, NodeGeneType.BIAS));
        }
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("nodes.nodeIdFactory", nodeIdFactory);
        state.put("nodes.biasFactories", biasFactories);
        state.put("nodes.activationFunctionFactories", activationFunctionFactories);
        state.put("nodes.inputNodeIds", inputNodeIds);
        state.put("nodes.outputNodeIds", outputNodeIds);
        state.put("nodes.biasNodeIds", biasNodeIds);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        nodeIdFactory = DualModeObject.switchMode(state.get("nodes.nodeIdFactory"), eventLoop != null);
        biasFactories = DualModeObject.switchModes(state.get("nodes.biasFactories"), eventLoop != null);
        activationFunctionFactories = DualModeObject.switchModes(state.get("nodes.activationFunctionFactories"), eventLoop != null);
        inputNodeIds = state.get("nodes.inputNodeIds");
        outputNodeIds = state.get("nodes.outputNodeIds");
        biasNodeIds = state.get("nodes.biasNodeIds");
    }
}
