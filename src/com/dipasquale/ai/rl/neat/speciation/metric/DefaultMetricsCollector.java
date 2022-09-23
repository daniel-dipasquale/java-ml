package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
public final class DefaultMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = 4466816617617350646L;
    private final boolean clearFitnessOnNext;
    private volatile boolean fitnessMetricsOutstanding = false;
    private final boolean clearGenerationsOnNext;
    private boolean generationMetricsOutstanding = false;
    private final boolean clearIterationsOnNext;

    private void collectSpeciesComposition(final MetricsContainer metricsContainer, final int age, final int stagnationPeriod, final boolean isStagnant) {
        metricsContainer.addSpeciesComposition(age, stagnationPeriod, isStagnant);
        generationMetricsOutstanding = true;
    }

    private void collectOrganismTopology(final MetricsContainer metricsContainer, final String speciesId, final int hiddenNodeGenes, final int connectionGenes) {
        metricsContainer.addOrganismTopology(speciesId, hiddenNodeGenes, connectionGenes);
        generationMetricsOutstanding = true;
    }

    @Override
    public void collectAllSpeciesCompositions(final MetricsContainer metricsContainer, final Iterable<Species> allSpecies, final int stagnationDropOffAge) {
        for (Species species : allSpecies) {
            collectSpeciesComposition(metricsContainer, species.getAge(), species.getStagnationPeriod(), species.isStagnant(stagnationDropOffAge));

            for (Organism organism : species.getOrganisms()) {
                collectOrganismTopology(metricsContainer, species.getId(), organism.getHiddenNodeGenes(), organism.getConnectionGenes());
            }
        }
    }

    @Override
    public void collectOrganismFitness(final MetricsContainer metricsContainer, final String speciesId, final float fitness) {
        metricsContainer.addOrganismFitness(speciesId, fitness);
        fitnessMetricsOutstanding = true;
    }

    @Override
    public void collectSpeciesFitness(final MetricsContainer metricsContainer, final float fitness) {
        metricsContainer.addSpeciesFitness(fitness);
        fitnessMetricsOutstanding = true;
    }

    @Override
    public void collectOrganismsKilled(final MetricsContainer metricsContainer, final String speciesId, final int count) {
        metricsContainer.addOrganismsKilled(speciesId, count);
        generationMetricsOutstanding = true;
    }

    @Override
    public void collectSpeciesExtinction(final MetricsContainer metricsContainer, final boolean extinct) {
        metricsContainer.addSpeciesExtinction(extinct);
        generationMetricsOutstanding = true;
    }

    @Override
    public void prepareNextFitnessEvaluation(final MetricsContainer metricsContainer) {
        if (!fitnessMetricsOutstanding) {
            return;
        }

        if (clearFitnessOnNext) {
            metricsContainer.clearFitnessEvaluations();
        }

        metricsContainer.commitFitness();
        fitnessMetricsOutstanding = false;
        generationMetricsOutstanding = true;
    }

    @Override
    public void prepareNextGeneration(final MetricsContainer metricsContainer, final int currentGeneration) {
        prepareNextFitnessEvaluation(metricsContainer);

        if (!generationMetricsOutstanding) {
            return;
        }

        if (clearGenerationsOnNext) {
            metricsContainer.clearPreviousGenerations();
        }

        metricsContainer.commitGeneration(currentGeneration);
        generationMetricsOutstanding = false;
    }

    @Override
    public void prepareNextIteration(final MetricsContainer metricsContainer, final int currentGeneration, final Map<Integer, IterationMetrics> allIterationMetrics, final int currentIteration) {
        prepareNextGeneration(metricsContainer, currentGeneration);

        if (clearIterationsOnNext) {
            allIterationMetrics.clear();
        }

        allIterationMetrics.put(currentIteration, metricsContainer.commitIteration());
    }
}
