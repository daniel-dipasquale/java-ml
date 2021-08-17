package com.dipasquale.ai.rl.neat.core;

public interface NeatActivator {
    int getGeneration();

    int getSpeciesCount();

    float getFitness();

    int getComplexity();

    float[] activate(float[] inputs);
}
