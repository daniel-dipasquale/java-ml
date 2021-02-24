package com.experimental.ai.rl.neat;

public interface Genome {
    int getComplexity();

    float[] activate(float[] input);
}
