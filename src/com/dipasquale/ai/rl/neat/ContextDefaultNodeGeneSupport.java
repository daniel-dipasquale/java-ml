package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultNodeGeneSupport<T extends Comparable<T>> implements Context.NodeGeneSupport<T> {
    private final Map<NodeGeneType, SequentialIdFactory<T>> sequentialIdFactories;
    private final Map<NodeGeneType, FloatFactory> biasFactories;
    private final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories;

    @Override
    public NodeGene<T> create(final NodeGeneType type) {
        T id = sequentialIdFactories.get(type).next();
        float bias = biasFactories.get(type).next();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).next();

        return new NodeGene<>(id, NodeGeneType.Hidden, bias, activationFunction);
    }
}
