package com.dipasquale.ai.rl.neat.genotype;

public interface Genome {
    String getId();

    int getComplexity();

    float[] activate(float[] input);
}
