package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuralNetworkSupportDefault implements Context.NeuralNetworkSupport {
    private final Factory factory;

    @Override
    public NeuralNetwork create(final Genome genome) {
        return factory.create(genome);
    }

    @FunctionalInterface
    interface Factory {
        NeuralNetwork create(Genome genome);
    }
}
