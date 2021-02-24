package com.experimental.ai.rl.neat;

interface NeuralNetwork {
    float[] activate(float[] input);

    void reset();
}
