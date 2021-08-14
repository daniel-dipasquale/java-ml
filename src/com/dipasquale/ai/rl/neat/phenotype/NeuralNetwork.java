package com.dipasquale.ai.rl.neat.phenotype;

public interface NeuralNetwork {
    float[] activate(float[] input);

    void reset();
}
