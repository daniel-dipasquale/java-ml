package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultNodeGeneSupport implements Context.NodeGeneSupport {
    private final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories;
    private final Map<NodeGeneType, FloatFactory> biasFactories;
    private final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories;

    @Override
    public NodeGene create(final NodeGeneType type) {
        SequentialId id = sequentialIdFactories.get(type).next();
        float bias = biasFactories.get(type).next();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).next();

        return new NodeGene(id, type, bias, activationFunction);
    }
}
