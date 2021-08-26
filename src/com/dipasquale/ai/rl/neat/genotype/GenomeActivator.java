package com.dipasquale.ai.rl.neat.genotype;

public interface GenomeActivator {
    String getId();

    int getGeneration();

    int getComplexity();

    float[] activate(float[] input);
}
