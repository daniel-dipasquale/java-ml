package com.dipasquale.ai.rl.neat;

@FunctionalInterface
interface NeuronPromoter<T extends Neuron> {
    Neuron promote(T neuron);
}
