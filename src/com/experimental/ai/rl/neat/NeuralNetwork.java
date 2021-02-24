package com.experimental.ai.rl.neat;

public interface NeuralNetwork {
    float[] activate(float[] input);

    void reset();
}
