package com.dipasquale.ai.rl.neat;

interface NeuralNetwork {
    float[] activate(float[] input);

    void reset();
}
