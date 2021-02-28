package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultNeuralNetworkSupport<T extends Comparable<T>> implements Context.NeuralNetworkSupport<T> {
    private final Factory<T> factory;

    @Override
    public NeuralNetwork create(final GenomeDefault<T> genome) {
        return factory.create(genome);
    }

    @FunctionalInterface
    interface Factory<T extends Comparable<T>> {
        NeuralNetwork create(GenomeDefault<T> genome);
    }
}
