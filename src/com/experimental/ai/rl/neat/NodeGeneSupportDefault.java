package com.experimental.ai.rl.neat;

import com.experimental.ai.common.ActivationFunction;
import com.experimental.ai.common.Counter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneSupportDefault<T extends Comparable<T>> implements Context.NodeGeneSupport<T> {
    private final Counter<T> forwardCounter;
    private final Counter<T> backwardCounter;
    private final BiasFactory biasFactory;
    private final ActivationFunctionFactory activationFunctionFactory;

    private T createNextId(final NodeGene.Type type) {
        T id = switch (type) {
            case Input, Hidden -> forwardCounter.next();

            default -> backwardCounter.next();
        };

        if (forwardCounter.current().compareTo(backwardCounter.current()) < 0) {
            return id;
        }

        throw new IllegalStateException("Unable to create more nodes, larger domain is needed");
    }

    @Override
    public NodeGene<T> create(final NodeGene.Type type) {
        return new NodeGene<>(createNextId(type), type, biasFactory.next(), activationFunctionFactory.next());
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
