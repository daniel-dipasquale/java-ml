package com.dipasquale.ai.rl.neat;

public interface NeatEvaluatorTrainer {
    int getGeneration();

    int getSpeciesCount();

    void restart();

    void train(NeatEvaluatorTrainingPolicy trainingPolicy);

    float getMaximumFitness();

    float[] activate(float[] input);

    void shutdown();
}
