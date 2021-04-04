package com.dipasquale.ai.rl.neat;

public interface NeatEvaluatorTrainer {
    int getGeneration();

    int getSpeciesCount();

    boolean train(NeatEvaluatorTrainingPolicy trainingPolicy);

    float getMaximumFitness();

    float[] activate(float[] input);
}
