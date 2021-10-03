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
    private final boolean clearGenerationsOnAdd;
    private final boolean clearIterationsOnAdd;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void collectSpeciesComposition(final MetricsContainer metricsContainer, final int age, final int stagnationPeriod, final boolean isStagnant) {
        metricsContainer.getGenerationMetrics().getSpeciesAge().add((float) age);
        metricsContainer.getGenerationMetrics().getSpeciesStagnationPeriod().add((float) stagnationPeriod);
        metricsContainer.getGenerationMetrics().getSpeciesStagnant().add(isStagnant ? 1f : 0f);
    }

    @Override
    public void collectOrganismTopology(final MetricsContainer metricsContainer, final String speciesId, final int hiddenNodes, final int connections) {
        metricsContainer.getGenerationMetrics().getOrganismsTopology().compute(speciesId, (sid, otm) -> {
            TopologyMetrics topologyMetrics = otm;

            if (topologyMetrics == null) {
                topologyMetrics = MetricsContainer.createTopologyMetrics(metricDatumFactory);
            }

            topologyMetrics.getHiddenNodes().add((float) hiddenNodes);
            topologyMetrics.getConnections().add((float) connections);
            metricsContainer.getGenerationMetrics().getSpeciesTopology().merge(topologyMetrics);

            return topologyMetrics;
        });
    }

    @Override
    public void flushSpeciesComposition(final MetricsContainer metricsContainer) {
        metricsContainer.getIterationMetrics().getSpeciesCount().add((float) metricsContainer.getGenerationMetrics().getOrganismsTopology().size());
    }

    @Override
    public void collectOrganismFitness(final MetricsContainer metricsContainer, final String speciesId, final float fitness) {
        metricsContainer.getFitnessMetrics().getOrganisms().compute(speciesId, (sid, o) -> {
            MetricDatum organisms = o;

            if (organisms == null) {
                organisms = metricDatumFactory.create();
            }

            organisms.add(fitness);

            return organisms;
        });
    }

    @Override
    public void collectSpeciesFitness(final MetricsContainer metricsContainer, final float fitness) {
        metricsContainer.getFitnessMetrics().getShared().add(fitness);
    }

    @Override
    public void prepareNextFitnessCalculation(final MetricsContainer metricsContainer) {
        metricsContainer.getFitnessMetrics().getOrganisms().values().forEach(metricsContainer.getFitnessMetrics().getAll()::merge);
        metricsContainer.getGenerationMetrics().getSpeciesAllFitness().merge(metricsContainer.getFitnessMetrics().getAll());
        metricsContainer.getGenerationMetrics().getSpeciesSharedFitness().merge(metricsContainer.getFitnessMetrics().getShared());

        if (clearFitnessOnAdd) {
            metricsContainer.getGenerationMetrics().getOrganismsFitness().clear();
        }

        metricsContainer.getGenerationMetrics().getOrganismsFitness().add(metricsContainer.getFitnessMetrics());
        metricsContainer.replaceFitnessMetrics();
    }

    @Override
    public void prepareNextGeneration(final MetricsContainer metricsContainer, final int currentGeneration) {
        if (clearGenerationsOnAdd) {
            metricsContainer.getIterationMetrics().getGenerations().clear();
        }

        metricsContainer.getIterationMetrics().getGenerations().put(currentGeneration, metricsContainer.getGenerationMetrics());
        metricsContainer.replaceGenerationMetrics();
    }

    @Override
    public void prepareNextIteration(final MetricsContainer metricsContainer, final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
        if (clearIterationsOnAdd) {
            iterationsMetrics.clear();
        }

        iterationsMetrics.put(currentIteration, metricsContainer.getIterationMetrics());
        metricsContainer.replaceIterationMetrics();
    }
}
