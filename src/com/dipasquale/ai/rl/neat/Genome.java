package com.dipasquale.ai.rl.neat;

public interface Genome {
    String getId();

    int getComplexity();

    float[] activate(float[] input);
}
