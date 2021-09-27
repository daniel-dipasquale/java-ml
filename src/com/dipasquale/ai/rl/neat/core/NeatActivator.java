package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;

import java.util.Map;

public interface NeatActivator {
    int getIteration();

    int getGeneration();

    int getSpeciesCount();

    int getCurrentHiddenNodes();

    int getCurrentConnections();

    float getMaximumFitness();

    Map<Integer, IterationMetrics> getMetrics();

    float[] activate(float[] inputs);
}
