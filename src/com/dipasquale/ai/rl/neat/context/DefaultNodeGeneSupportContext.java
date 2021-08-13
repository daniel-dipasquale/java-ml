/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.function.activation.ActivationFunctionFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public final class DefaultNodeGeneSupportContext implements Context.NodeGeneSupport {
    private Map<NodeGeneType, ObjectSwitcher<FloatFactory>> biasFactories;
    private Map<NodeGeneType, ObjectSwitcher<ActivationFunctionFactory>> activationFunctionFactories;
    private int inputs;
    private int outputs;
    private int biases;

    @Override
    public NodeGene create(final SequentialId id, final NodeGeneType type) {
        float bias = biasFactories.get(type).getObject().create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).getObject().create();

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
        state.put("nodes.activationFunctionFactories", activationFunctionFactories);
        state.put("nodes.inputs", inputs);
        state.put("nodes.outputs", outputs);
        state.put("nodes.biases", biases);
    }

    private static <T> Map<NodeGeneType, ObjectSwitcher<T>> loadFactories(final Map<NodeGeneType, ObjectSwitcher<T>> factories, final IterableEventLoop eventLoop) {
        for (ObjectSwitcher<T> factory : factories.values()) {
            factory.switchObject(eventLoop != null);
        }

        return factories;
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        biasFactories = loadFactories(state.get("nodes.biasFactories"), eventLoop);
        activationFunctionFactories = loadFactories(state.get("nodes.activationFunctionFactories"), eventLoop);
        inputs = state.get("nodes.inputs");
        outputs = state.get("nodes.outputs");
        biases = state.get("nodes.biases");
    }
}
