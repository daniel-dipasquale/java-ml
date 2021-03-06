package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface NeuralNetworkFactory<T extends Comparable<T>> {
    NeuralNetwork create(GenomeDefault<T> genome);
}
