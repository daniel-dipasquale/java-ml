package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface NeuralNetworkFactory {
    NeuralNetwork create(GenomeDefault genome);
}
