package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public final class ContextDefaultNodeGeneSupport implements Context.NodeGeneSupport {
    private Map<NodeGeneType, FloatFactory> biasFactories;
    private Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories;
    private int inputs;
    private int outputs;
    private int biases;

    @Override
    public NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).create();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public int size(final NodeGeneType type) {
        return switch (type) {
            case INPUT -> inputs;

            case OUTPUT -> outputs;

            case BIAS -> biases;

            case HIDDEN -> throw new IllegalArgumentException("hidden is will always be 0 for the genesis genome");
        };
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("nodes.biasFactories", biasFactories);
        state.put("nodes.activationFunctionProviders", activationFunctionFactories);
        state.put("nodes.inputs", inputs);
        state.put("nodes.outputs", outputs);
        state.put("nodes.biases", biases);
    }

    private static Map<NodeGeneType, FloatFactory> loadBiasFactories(final Map<NodeGeneType, FloatFactory> biasFactories, final EventLoopIterable eventLoop) {
        Map<NodeGeneType, FloatFactory> biasFactoriesFixed = new HashMap<>();

        for (Map.Entry<NodeGeneType, FloatFactory> entry : biasFactories.entrySet()) {
            biasFactoriesFixed.put(entry.getKey(), entry.getValue().selectContended(eventLoop != null));
        }

        return biasFactoriesFixed;
    }

    private static Map<NodeGeneType, ActivationFunctionFactory> loadActivationFunctionFactories(final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final EventLoopIterable eventLoop) {
        Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactoriesFixed = new HashMap<>();

        for (Map.Entry<NodeGeneType, ActivationFunctionFactory> entry : activationFunctionFactories.entrySet()) {
            activationFunctionFactoriesFixed.put(entry.getKey(), entry.getValue().selectContended(eventLoop != null));
        }

        return activationFunctionFactoriesFixed;
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        biasFactories = loadBiasFactories(state.get("nodes.biasFactories"), eventLoop);
        activationFunctionFactories = loadActivationFunctionFactories(state.get("nodes.activationFunctionProviders"), eventLoop);
        inputs = state.get("nodes.inputs");
        outputs = state.get("nodes.outputs");
        biases = state.get("nodes.biases");
    }
}
