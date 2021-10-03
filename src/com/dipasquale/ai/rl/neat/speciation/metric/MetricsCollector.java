package com.dipasquale.ai.rl.neat.speciation.metric;

import java.util.Map;

public interface MetricsCollector {
    boolean isEnabled();

    void collectSpeciesComposition(MetricsContainer metricsContainer, int age, int stagnationPeriod, boolean isStagnant);

    void collectOrganismTopology(MetricsContainer metricsContainer, String speciesId, int hiddenNodes, int connections);

    void flushSpeciesComposition(MetricsContainer metricsContainer);

    void collectOrganismFitness(MetricsContainer metricsContainer, String speciesId, float fitness);

    void collectSpeciesFitness(MetricsContainer metricsContainer, float fitness);

    void prepareNextFitnessCalculation(MetricsContainer metricsContainer);

    void prepareNextGeneration(MetricsContainer metricsContainer, int currentGeneration);

    void prepareNextIteration(MetricsContainer metricsContainer, Map<Integer, IterationMetrics> iterationsMetrics, int currentIteration);
}
