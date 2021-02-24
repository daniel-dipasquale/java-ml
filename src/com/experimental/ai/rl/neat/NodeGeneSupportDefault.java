package com.experimental.ai.rl.neat;

import com.experimental.ai.ActivationFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneSupportDefault<T extends Comparable<T>> implements Context.NodeGeneSupport<T> {
    private final Counter<T> forwardCounter;
    private final Counter<T> backwardCounter;
    private final Map<T, NodeGene<T>> nodes;

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

    private float nextBias() {
        return 0f;
    }

    private ActivationFunction nextActivationFunction() {
        return null;
    }

    @Override
    public NodeGene<T> get(final T id) {
        return nodes.get(id);
    }

    @Override
    public NodeGene<T> create(final NodeGene.Type type) {
        NodeGene<T> node = new NodeGene<>(createNextId(type), type, nextBias(), nextActivationFunction());

        nodes.put(node.getId(), node);

        return node;
    }
}
