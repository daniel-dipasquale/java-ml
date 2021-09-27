package com.dipasquale.ai.rl.neat.speciation.metric;

import java.util.Map;

public interface MetricDataCollector {
    FitnessMetricData getCurrentFitness();

    GenerationMetricData getCurrentGeneration();

    IterationMetricData getCurrentIteration();

    void addOrganismTopology(String speciesId, int hiddenNodes, int connections);

    void addOrganismFitness(String speciesId, float fitness);

    void addSpeciesAttributes(int age, int stagnationPeriod, boolean isStagnant);

    void addSpeciesFitness(float fitness);

    void prepareNextFitnessCalculation();

    void prepareNextGeneration(int currentGeneration);

    void prepareNextIteration(Map<Integer, IterationMetricData> iterationsMetrics, int currentIteration);
}
