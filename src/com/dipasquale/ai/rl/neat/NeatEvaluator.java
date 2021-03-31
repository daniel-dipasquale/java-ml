package com.dipasquale.ai.rl.neat;

public interface NeatEvaluator {
    int getGeneration();

    int getSpeciesCount();

    void evaluateFitness();

    void evolve();

    float getMaximumFitness();

    float[] activate(float[] input);
}
