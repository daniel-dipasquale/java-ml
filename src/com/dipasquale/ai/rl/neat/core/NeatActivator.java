package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetricData;

import java.util.Map;

public interface NeatActivator {
    int getIteration();

    int getGeneration();

    int getSpeciesCount();

    int getConnections();

    float getFitness();

    Map<Integer, IterationMetricData> getMetrics();

    float[] activate(float[] inputs);
}
