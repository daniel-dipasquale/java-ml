package com.experimental.ai.rl.neat;

import com.experimental.ai.common.ActivationFunction;
import com.experimental.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneSupportDefault<T extends Comparable<T>> implements Context.NodeGeneSupport<T> {
    private final Map<NodeGene.Type, SequentialIdFactory<T>> sequentialIdFactories;
    private final BiasFactory biasFactory;
    private final ActivationFunctionFactory activationFunctionFactory;

    @Override
    public NodeGene<T> create(final NodeGene.Type type) {
        T id = sequentialIdFactories.get(type).next();
        float bias = biasFactory.next();
        ActivationFunction activationFunction = activationFunctionFactory.next();

        return new NodeGene<>(id, type, bias, activationFunction);
    }

    @FunctionalInterface
    interface BiasFactory {
        float next();
    }

    @FunctionalInterface
    interface ActivationFunctionFactory {
        ActivationFunction next();
    }
}
