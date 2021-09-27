package com.dipasquale.ai.rl.neat.speciation.metric;

import java.util.Map;

public interface MetricsCollector {
    FitnessMetrics getFitnessMetrics();

    GenerationMetrics getGenerationMetrics();

    IterationMetrics getIterationMetrics();

    void collectSpeciesComposition(int age, int stagnationPeriod, boolean isStagnant);

    void collectOrganismTopology(String speciesId, int hiddenNodes, int connections);

    void flushSpeciesComposition();

    void collectOrganismFitness(String speciesId, float fitness);

    void collectSpeciesFitness(float fitness);

    void prepareNextFitnessCalculation();

    void prepareNextGeneration(int currentGeneration);

    void prepareNextIteration(Map<Integer, IterationMetrics> iterationsMetrics, int currentIteration);
}
