package com.dipasquale.ai.rl.neat.speciation.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class NoopMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -3442321706503755754L;

    @Override
    public void collectSpeciesComposition(final MetricsContainer metricsContainer, final int age, final int stagnationPeriod, final boolean isStagnant) {
    }

    @Override
    public void collectOrganismTopology(final MetricsContainer metricsContainer, final String speciesId, final int hiddenNodes, final int connections) {
    }

    @Override
    public void collectOrganismFitness(final MetricsContainer metricsContainer, final String speciesId, final float fitness) {
    }

    @Override
    public void collectSpeciesFitness(final MetricsContainer metricsContainer, final float fitness) {
    }

    @Override
    public void collectOrganismsKilled(final MetricsContainer metricsContainer, final String speciesId, final int count) {
    }

    @Override
    public void collectSpeciesExtinction(final MetricsContainer metricsContainer, final boolean extinct) {
    }

    @Override
    public void prepareNextFitnessCalculation(final MetricsContainer metricsContainer) {
    }

    @Override
    public void prepareNextGeneration(final MetricsContainer metricsContainer, final int currentGeneration) {
    }

    @Override
    public void prepareNextIteration(final MetricsContainer metricsContainer, final int currentGeneration, final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
    }
}
