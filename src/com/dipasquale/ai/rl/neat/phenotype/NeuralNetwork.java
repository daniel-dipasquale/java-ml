package com.dipasquale.ai.rl.neat.phenotype;

@FunctionalInterface
public interface NeuralNetwork {
    float[] activate(float[] input);
}
