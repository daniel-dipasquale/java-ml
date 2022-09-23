package com.dipasquale.ai.rl.neat.speciation.metric.concurrent;

import com.dipasquale.ai.rl.neat.speciation.metric.AbstractMetricsContainer;
import com.dipasquale.ai.rl.neat.speciation.metric.FitnessMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.synchronization.IsolatedThreadStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class IsolatedMetricContainer extends AbstractMetricsContainer {
    private final MetricDatumFactory metricDatumFactory;
    private final IsolatedThreadStorage<Map<String, MetricDatum>> fitnessMetrics_organisms;
    private final MetricDatum fitnessMetrics_species;

    public IsolatedMetricContainer(final Set<Long> threadIds, final MetricDatumFactory metricDatumFactory, final IterationMetrics iterationMetrics) {
        super(metricDatumFactory, iterationMetrics);
        this.metricDatumFactory = metricDatumFactory;
        this.fitnessMetrics_organisms = new IsolatedThreadStorage<>(threadIds);
        this.fitnessMetrics_species = metricDatumFactory.create();
    }

    private MetricDatum provideOrganisms(final MetricDatum oldOrganisms, final float fitness) {
        MetricDatum organisms = oldOrganisms;

        if (organisms == null) {
            organisms = metricDatumFactory.create();
        }

        organisms.add(fitness);

        return organisms;
    }

    @Override
    public void addOrganismFitness(final String speciesId, final float fitness) {
        fitnessMetrics_organisms.computeIfAbsent(HashMap::new).compute(speciesId, (__, oldOrganisms) -> provideOrganisms(oldOrganisms, fitness));
    }

    @Override
    public void addSpeciesFitness(final float fitness) {
        fitnessMetrics_species.add(fitness);
    }

    private FitnessMetrics createFitnessMetrics() {
        FitnessMetrics fitnessMetrics = new FitnessMetrics(metricDatumFactory.create());
        Map<String, MetricDatum> organisms = fitnessMetrics.getOrganisms();

        for (Map<String, MetricDatum> collectedOrganisms : fitnessMetrics_organisms) {
            for (Map.Entry<String, MetricDatum> entry : collectedOrganisms.entrySet()) {
                organisms.computeIfAbsent(entry.getKey(), __ -> metricDatumFactory.create()).merge(entry.getValue());
            }
        }

        fitnessMetrics.getSpecies().merge(fitnessMetrics_species);

        return fitnessMetrics;
    }

    @Override
    public void commitFitness() {
        addFitness(createFitnessMetrics());
        fitnessMetrics_organisms.clear();
        fitnessMetrics_species.clear();
    }
}
