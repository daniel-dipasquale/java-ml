package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
public final class ConfigurableMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = 4466816617617350646L;
    private final MetricDatumFactory metricDatumFactory;
    private final boolean clearFitnessOnAdd;
    private boolean fitnessMetricsOutstanding = false;
    private final boolean clearGenerationsOnAdd;
    private boolean generationMetricsOutstanding = false;
    private final boolean clearIterationsOnAdd;

    @Override
    public void collectSpeciesComposition(final MetricsContainer metricsContainer, final int age, final int stagnationPeriod, final boolean isStagnant) {
        metricsContainer.getGenerationMetrics().getSpeciesAge().add((float) age);
        metricsContainer.getGenerationMetrics().getSpeciesStagnationPeriod().add((float) stagnationPeriod);
        metricsContainer.getGenerationMetrics().getSpeciesStagnant().add(isStagnant ? 1f : 0f);
        generationMetricsOutstanding = true;
    }

    @Override
    public void collectOrganismTopology(final MetricsContainer metricsContainer, final String speciesId, final int hiddenNodes, final int connections) {
        metricsContainer.getGenerationMetrics().getOrganismsTopology().compute(speciesId, (__, oldTopologyMetrics) -> {
            TopologyMetrics topologyMetrics = oldTopologyMetrics;

            if (topologyMetrics == null) {
                topologyMetrics = MetricsContainer.createTopologyMetrics(metricDatumFactory);
            }

            topologyMetrics.getHiddenNodes().add((float) hiddenNodes);
            topologyMetrics.getConnections().add((float) connections);

            return topologyMetrics;
        });

        generationMetricsOutstanding = true;
    }

    @Override
    public void collectOrganismFitness(final MetricsContainer metricsContainer, final String speciesId, final float fitness) {
        metricsContainer.getFitnessMetrics().getOrganisms().compute(speciesId, (__, oldOrganisms) -> {
            MetricDatum organisms = oldOrganisms;

            if (organisms == null) {
                organisms = metricDatumFactory.create();
            }

            organisms.add(fitness);

            return organisms;
        });

        fitnessMetricsOutstanding = true;
    }

    @Override
    public void collectSpeciesFitness(final MetricsContainer metricsContainer, final float fitness) {
        metricsContainer.getFitnessMetrics().getShared().add(fitness);
        fitnessMetricsOutstanding = true;
    }

    @Override
    public void collectOrganismsKilled(final MetricsContainer metricsContainer, final String speciesId, final int count) {
        metricsContainer.getGenerationMetrics().getOrganismsKilled().compute(speciesId, (__, oldOrganismsKilled) -> {
            MetricDatum organismsKilled = oldOrganismsKilled;

            if (organismsKilled == null) {
                organismsKilled = metricDatumFactory.create();
            }

            organismsKilled.add((float) count);

            return organismsKilled;
        });

        generationMetricsOutstanding = true;
    }

    @Override
    public void collectSpeciesExtinction(final MetricsContainer metricsContainer, final boolean extinct) {
        metricsContainer.getGenerationMetrics().getSpeciesExtinct().add(extinct ? 1f : 0f);
        generationMetricsOutstanding = true;
    }

    @Override
    public void prepareNextFitnessCalculation(final MetricsContainer metricsContainer) {
        if (!fitnessMetricsOutstanding) {
            return;
        }

        fitnessMetricsOutstanding = false;

        if (clearFitnessOnAdd) {
            metricsContainer.getGenerationMetrics().getFitnessCalculations().clear();
        }

        metricsContainer.getGenerationMetrics().getFitnessCalculations().add(metricsContainer.getFitnessMetrics());
        metricsContainer.replaceFitnessMetrics();
        generationMetricsOutstanding = true;
    }

    @Override
    public void prepareNextGeneration(final MetricsContainer metricsContainer, final int currentGeneration) {
        prepareNextFitnessCalculation(metricsContainer);

        if (!generationMetricsOutstanding) {
            return;
        }

        generationMetricsOutstanding = false;

        if (clearGenerationsOnAdd) {
            metricsContainer.getIterationMetrics().getGenerations().clear();
        }

        metricsContainer.getIterationMetrics().getGenerations().put(currentGeneration, metricsContainer.getGenerationMetrics());
        metricsContainer.replaceGenerationMetrics();
    }

    @Override
    public void prepareNextIteration(final MetricsContainer metricsContainer, final int currentGeneration, final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
        prepareNextGeneration(metricsContainer, currentGeneration);

        if (clearIterationsOnAdd) {
            iterationsMetrics.clear();
        }

        iterationsMetrics.put(currentIteration, metricsContainer.getIterationMetrics());
        metricsContainer.replaceIterationMetrics();
    }
}
