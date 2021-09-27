package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
public final class ConfigurableMetricDataCollector implements MetricDataCollector, Serializable {
    @Serial
    private static final long serialVersionUID = 4466816617617350646L;
    private final MapFactory mapFactory;
    private final ObjectFactory<MetricDatum> metricDatumFactory;
    private final boolean clearFitnessOnAdd;
    private final boolean clearGenerationsOnAdd;
    private final boolean clearIterationsOnAdd;
    private GenerationMetricData currentGenerationMetrics;
    private FitnessMetricData currentFitnessMetrics;
    private IterationMetricData currentIterationMetrics;

    public ConfigurableMetricDataCollector(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory, final boolean clearFitnessOnAdd, final boolean clearGenerationsOnAdd, final boolean clearIterationsOnAdd) {
        this(mapFactory, metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd, createGenerationMetrics(mapFactory, metricDatumFactory), createFitnessMetrics(mapFactory, metricDatumFactory), createIterationMetrics(mapFactory, metricDatumFactory));
    }

    private static TopologyMetricData createTopologyMetrics(final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new TopologyMetricData(metricDatumFactory.create(), metricDatumFactory.create());
    }

    private static GenerationMetricData createGenerationMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new GenerationMetricData(mapFactory.create(), createTopologyMetrics(metricDatumFactory), new ArrayList<>(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create());
    }

    private static FitnessMetricData createFitnessMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new FitnessMetricData(mapFactory.create(), metricDatumFactory.create());
    }

    private static IterationMetricData createIterationMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new IterationMetricData(mapFactory.create(), metricDatumFactory.create());
    }

    @Override
    public FitnessMetricData getCurrentFitness() {
        return currentFitnessMetrics;
    }

    @Override
    public GenerationMetricData getCurrentGeneration() {
        return currentGenerationMetrics;
    }

    @Override
    public IterationMetricData getCurrentIteration() {
        return currentIterationMetrics;
    }

    private TopologyMetricData createTopologyMetrics() {
        return createTopologyMetrics(metricDatumFactory);
    }

    @Override
    public void addOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
        TopologyMetricData organismsTopologyMetrics = currentGenerationMetrics.getOrganismsTopology().computeIfAbsent(speciesId, sid -> createTopologyMetrics());

        organismsTopologyMetrics.getHiddenNodes().add((float) hiddenNodes);
        organismsTopologyMetrics.getConnections().add((float) connections);
    }

    @Override
    public void addOrganismFitness(final String speciesId, final float fitness) {
        currentFitnessMetrics.getOrganisms().computeIfAbsent(speciesId, sid -> metricDatumFactory.create()).add(fitness);
    }

    @Override
    public void addSpeciesAttributes(final int age, final int stagnationPeriod, final boolean isStagnant) {
        currentGenerationMetrics.getSpeciesAge().add((float) age);
        currentGenerationMetrics.getSpeciesStagnationPeriod().add((float) stagnationPeriod);
        currentGenerationMetrics.getSpeciesStagnating().add(isStagnant ? 1f : 0f);
    }

    @Override
    public void addSpeciesFitness(final float fitness) {
        currentFitnessMetrics.getSpecies().add(fitness);
    }

    private FitnessMetricData createFitnessMetrics() {
        return createFitnessMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextFitnessCalculation() {
        for (MetricDatum organisms : currentFitnessMetrics.getOrganisms().values()) {
            currentFitnessMetrics.getSpecies().merge(organisms);
        }

        if (clearFitnessOnAdd) {
            currentGenerationMetrics.getOrganismsFitness().clear();
        }

        currentGenerationMetrics.getOrganismsFitness().add(currentFitnessMetrics);
        currentFitnessMetrics = createFitnessMetrics();
    }

    private GenerationMetricData createGenerationMetrics() {
        return createGenerationMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
        for (TopologyMetricData organismsTopologyMetrics : currentGenerationMetrics.getOrganismsTopology().values()) {
            TopologyMetricData speciesTopology = currentGenerationMetrics.getSpeciesTopology();

            speciesTopology.merge(organismsTopologyMetrics);
        }

        currentIterationMetrics.getSpecies().add(currentGenerationMetrics.getOrganismsTopology().size());

        if (clearGenerationsOnAdd) {
            currentIterationMetrics.getGenerations().clear();
        }

        currentIterationMetrics.getGenerations().put(currentGeneration, currentGenerationMetrics);
        currentGenerationMetrics = createGenerationMetrics();
    }

    private IterationMetricData createIterationMetrics() {
        return createIterationMetrics(mapFactory, metricDatumFactory);
    }

    @Override
    public void prepareNextIteration(final Map<Integer, IterationMetricData> iterationsMetrics, final int currentIteration) {
        if (clearIterationsOnAdd) {
            iterationsMetrics.clear();
        }

        iterationsMetrics.put(currentIteration, currentIterationMetrics);
        currentIterationMetrics = createIterationMetrics();
    }
}
