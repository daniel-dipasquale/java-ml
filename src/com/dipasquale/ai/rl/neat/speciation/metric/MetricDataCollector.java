package com.dipasquale.ai.rl.neat.speciation.metric;

import java.util.Map;

public interface MetricDataCollector {
    void addTopology(String speciesId, int hiddenNodes, int connections);

    void addFitness(String speciesId, float fitness);

    void prepareNextFitnessCalculation();

    void prepareNextGeneration(int currentGeneration);

    void prepareNextIteration(Map<Integer, MetricData> allMetrics, int iteration);
}
