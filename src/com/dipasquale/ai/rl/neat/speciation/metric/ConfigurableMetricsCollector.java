package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
public final class ConfigurableMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = 4466816617617350646L;
    private final MapFactory mapFactory;
    private final MetricDatumFactory metricDatumFactory;
    private final boolean clearFitnessOnAdd;
    private final boolean clearGenerationsOnAdd;
    private final boolean clearIterationsOnAdd;
    @Getter
    private GenerationMetrics generationMetrics;
    @Getter
    private FitnessMetrics fitnessMetrics;
    @Getter
    private IterationMetrics iterationMetrics;

    public ConfigurableMetricsCollector(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory, final boolean clearFitnessOnAdd, final boolean clearGenerationsOnAdd, final boolean clearIterationsOnAdd) {
        this(mapFactory, metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd, createGenerationMetrics(mapFactory, metricDatumFactory), createFitnessMetrics(mapFactory, metricDatumFactory), createIterationMetrics(mapFactory, metricDatumFactory));
    }

    private static TopologyMetrics createTopologyMetrics(final MetricDatumFactory metricDatumFactory) {
        return new TopologyMetrics(metricDatumFactory.create(), metricDatumFactory.create());
    }

    private static GenerationMetrics createGenerationMetrics(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        return new GenerationMetrics(mapFactory.create(), createTopologyMetrics(metricDatumFactory), new ArrayList<>(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create());
    }

    private static FitnessMetrics createFitnessMetrics(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        return new FitnessMetrics(mapFactory.create(), metricDatumFactory.create(), metricDatumFactory.create());
    }

    private static IterationMetrics createIterationMetrics(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        return new IterationMetrics(mapFactory.create(), metricDatumFactory.create());
    }

    private TopologyMetrics createTopologyMetrics() {
        return createTopologyMetrics(metricDatumFactory);
    }

    @Override
    public void collectSpeciesComposition(final int age, final int stagnationPeriod, final boolean isStagnant) {
        generationMetrics.getSpeciesAge().add((float) age);
        generationMetrics.getSpeciesStagnationPeriod().add((float) stagnationPeriod);
        generationMetrics.getSpeciesStagnant().add(isStagnant ? 1f : 0f);
    }

    @Override
    public void collectOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
        generationMetrics.getOrganismsTopology().compute(speciesId, (sid, otm) -> {
            TopologyMetrics topologyMetrics = otm;

            if (topologyMetrics == null) {
                topologyMetrics = createTopologyMetrics();
            }

            topologyMetrics.getHiddenNodes().add((float) hiddenNodes);
            topologyMetrics.getConnections().add((float) connections);
            generationMetrics.getSpeciesTopology().merge(topologyMetrics);

            return topologyMetrics;
        });
    }

    @Override
    public void flushSpeciesComposition() {
        iterationMetrics.getSpeciesCount().add((float) generationMetrics.getOrganismsTopology().size());
    }

    @Override
    public void collectOrganismFitness(final String speciesId, final float fitness) {
        fitnessMetrics.getOrganisms().compute(speciesId, (sid, o) -> {
            MetricDatum organisms = o;

            if (organisms == null) {
                organisms = metricDatumFactory.create();
            }

            organisms.add(fitness);
            fitnessMetrics.getAll().merge(organisms);

            return organisms;
        });
    }

    @Override
    public void collectSpeciesFitness(final float fitness) {
        fitnessMetrics.getShared().add(fitness);
    }

    private FitnessMetrics createFitnessMetrics() {
        return createFitnessMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextFitnessCalculation() {
        generationMetrics.getSpeciesAllFitness().merge(fitnessMetrics.getAll());
        generationMetrics.getSpeciesSharedFitness().merge(fitnessMetrics.getShared());

        if (clearFitnessOnAdd) {
            generationMetrics.getOrganismsFitness().clear();
        }

        generationMetrics.getOrganismsFitness().add(fitnessMetrics);
        fitnessMetrics = createFitnessMetrics();
    }

    private GenerationMetrics createGenerationMetrics() {
        return createGenerationMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
        if (clearGenerationsOnAdd) {
            iterationMetrics.getGenerations().clear();
        }

        iterationMetrics.getGenerations().put(currentGeneration, generationMetrics);
        generationMetrics = createGenerationMetrics();
    }

    private IterationMetrics createIterationMetrics() {
        return createIterationMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextIteration(final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
        if (clearIterationsOnAdd) {
            iterationsMetrics.clear();
        }

        iterationsMetrics.put(currentIteration, iterationMetrics);
        iterationMetrics = createIterationMetrics();
    }
}
