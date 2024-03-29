package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
interface NeuronStateGroupFactory {
    NeuronStateGroup create(NeatNeuronMemory neuronMemory);
}
