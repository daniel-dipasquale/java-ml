package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;

public interface NeatState {
    int getIteration();

    int getGeneration();

    int getSpeciesCount();

    Genome getChampionGenome();

    float getMaximumFitness();

    MetricsViewer createMetricsViewer();
}
