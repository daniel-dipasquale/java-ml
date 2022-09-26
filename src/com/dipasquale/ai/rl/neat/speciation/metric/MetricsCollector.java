package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.Species;

import java.util.Map;

public interface MetricsCollector {
    void collectAllSpeciesCompositions(MetricsContainer metricsContainer, Iterable<Species> allSpecies);

    void collectOrganismFitness(MetricsContainer metricsContainer, String speciesId, float fitness);

    void collectSpeciesFitness(MetricsContainer metricsContainer, float fitness);

    void collectOrganismsKilled(MetricsContainer metricsContainer, String speciesId, int count);

    void collectSpeciesExtinction(MetricsContainer metricsContainer, boolean extinct);

    void prepareNextFitnessEvaluation(MetricsContainer metricsContainer);

    void prepareNextGeneration(MetricsContainer metricsContainer, int currentGeneration);

    void prepareNextIteration(MetricsContainer metricsContainer, int currentGeneration, Map<Integer, IterationMetrics> allIterationMetrics, int currentIteration);
}
