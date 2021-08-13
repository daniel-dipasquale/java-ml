/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
public interface NeuronPromoter<T extends Neuron> {
    Neuron promote(T neuron);
}
