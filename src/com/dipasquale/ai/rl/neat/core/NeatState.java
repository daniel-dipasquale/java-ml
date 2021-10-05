package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;

import java.util.Map;

public interface NeatState {
    int getIteration();

    int getGeneration();

    int getSpeciesCount();

    Genome getChampionGenome();

    float getMaximumFitness();

    Map<Integer, IterationMetrics> getMetrics();
}
