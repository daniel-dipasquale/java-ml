package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionProvider;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContextDefaultNodeGeneSupport implements Context.NodeGeneSupport {
    private Map<NodeGeneType, FloatFactory> biasFactories;
    private Map<NodeGeneType, ActivationFunctionProvider> activationFunctionProviders;
    private int inputs;
    private int outputs;
    private int biases;

    public ContextDefaultNodeGeneSupport(final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionProvider> activationFunctionProviders, final int inputs, final int outputs, final int biases) {
        this.biasFactories = biasFactories;
        this.activationFunctionProviders = activationFunctionProviders;
        this.inputs = inputs;
        this.outputs = outputs;
        this.biases = biases;
    }

    @Override
    public NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionProviders.get(type).get();

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
        state.put("nodes.activationFunctionProviders", activationFunctionProviders);
        state.put("nodes.inputs", inputs);
        state.put("nodes.outputs", outputs);
        state.put("nodes.biases", biases);
    }

    public void load(final SerializableInteroperableStateMap state) {
        biasFactories = state.get("nodes.biasFactories");
        activationFunctionProviders = state.get("nodes.activationFunctionProviders");
        inputs = state.get("nodes.inputs");
        outputs = state.get("nodes.outputs");
        biases = state.get("nodes.biases");
    }
}
