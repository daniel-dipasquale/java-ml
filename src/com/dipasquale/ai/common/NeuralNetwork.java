package com.dipasquale.ai.common;

public interface NeuralNetwork<T extends NeuronMemory> {
    T createMemory();

    float[] activate(float[] input, T neuronMemory);
}
