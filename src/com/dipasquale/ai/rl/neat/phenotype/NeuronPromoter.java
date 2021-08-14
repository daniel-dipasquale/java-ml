package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
public interface NeuronPromoter<T extends Neuron> {
    Neuron promote(T neuron);
}
