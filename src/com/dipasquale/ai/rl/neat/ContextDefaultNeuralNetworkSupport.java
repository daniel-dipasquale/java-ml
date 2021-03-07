package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultNeuralNetworkSupport implements Context.NeuralNetworkSupport {
    private final NeuralNetworkFactory factory;

    @Override
    public NeuralNetwork create(final GenomeDefault genome) {
        return factory.create(genome);
    }
}
