package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.FloatFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class ContextDefaultNodeGeneSupport implements Context.NodeGeneSupport {
    private final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories;
    private final Map<NodeGeneType, FloatFactory> biasFactories;
    private final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories;

    @Override
    public NodeGene create(final NodeGeneType type) {
        SequentialId id = sequentialIdFactories.get(type).next();
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).next();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public void reset() {
        sequentialIdFactories.values().forEach(SequentialIdFactory::reset);
    }
}
