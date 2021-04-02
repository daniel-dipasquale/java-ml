package com.dipasquale.ai.rl.neat;

public interface NeatEvaluator {
    int getGeneration();

    int getSpeciesCount();

    void evaluateFitness();

    void evolve();

    void restart();

    float getMaximumFitness();

    float[] activate(float[] input);

    void shutdown();
}
