package com.dipasquale.ai.rl.neat.speciation.metric;

public interface MetricsContainer {
    void addSpeciesComposition(int age, int stagnationPeriod, boolean isStagnant);

    void addOrganismTopology(String speciesId, int hiddenNodeGenes, int connectionGenes);

    void addOrganismFitness(String speciesId, float fitness);

    void addSpeciesFitness(float fitness);

    void addOrganismsKilled(String speciesId, int count);

    void addSpeciesExtinction(boolean extinct);

    void clearFitnessEvaluations();

    void commitFitness();

    void clearPreviousGenerations();

    void commitGeneration(int generation);

    IterationMetrics commitIteration();

    IterationMetrics createInterimIterationCopy(int currentGeneration);

    IterationMetrics createIterationCopy();
}
