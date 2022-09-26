package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.Species;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class NoopMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -3442321706503755754L;
    private static final NoopMetricsCollector INSTANCE = new NoopMetricsCollector();

    public static NoopMetricsCollector getInstance() {
        return INSTANCE;
    }

    @Override
    public void collectAllSpeciesCompositions(final MetricsContainer metricsContainer, final Iterable<Species> allSpecies) {
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
    public void prepareNextFitnessEvaluation(final MetricsContainer metricsContainer) {
    }

    @Override
    public void prepareNextGeneration(final MetricsContainer metricsContainer, final int currentGeneration) {
    }

    @Override
    public void prepareNextIteration(final MetricsContainer metricsContainer, final int currentGeneration, final Map<Integer, IterationMetrics> allIterationMetrics, final int currentIteration) {
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
