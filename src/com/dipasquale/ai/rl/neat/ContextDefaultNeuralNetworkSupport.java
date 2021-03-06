package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultNeuralNetworkSupport<T extends Comparable<T>> implements Context.NeuralNetworkSupport<T> {
    private final NeuralNetworkFactory<T> factory;

    @Override
    public NeuralNetwork create(final GenomeDefault<T> genome) {
        return factory.create(genome);
    }
}
