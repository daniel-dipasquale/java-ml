package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
interface NeuronValueGroupFactory {
    NeuronValueGroup create(NeuronMemory neuronMemory);
}
