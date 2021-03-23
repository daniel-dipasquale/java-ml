package com.dipasquale.ai.rl.neat;

public interface NeatCollective {
    int getGeneration();

    int getSpeciesCount();

    void testFitness();

    void evolve();

    float[] activate(float[] input);

    float getMaximumFitness();
}
