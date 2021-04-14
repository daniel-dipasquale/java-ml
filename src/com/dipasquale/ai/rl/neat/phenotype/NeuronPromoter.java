package com.dipasquale.ai.rl.neat.phenotype;

import java.io.Serializable;

@FunctionalInterface
public interface NeuronPromoter<T extends Neuron> extends Serializable {
    Neuron promote(T neuron);
}
