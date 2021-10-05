package com.dipasquale.ai.rl.neat.phenotype;

public interface NeuralNetwork {
    NeuronMemory createMemory();

    float[] activate(float[] input, NeuronMemory neuronMemory);
}
