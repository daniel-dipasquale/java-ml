package com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.FitnessMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.TopologyMetricData;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeConfigurableMetricDataCollector extends DualModeMetricDataCollector {
    @Serial
    private static final long serialVersionUID = -6667927006892601411L;
    private boolean parallel;
    private final ObjectProfile<MapFactory> mapFactoryProfile;
    private final ObjectFactory<MetricDatum> metricDatumFactory;
    private final boolean clearGenerationsOnAdd;
    private final boolean clearIterationsOnAdd;
    private GenerationMetricData generationMetrics;
    private FitnessMetricData fitnessMetrics;
    private MetricData metrics;

    @Builder
    private DualModeConfigurableMetricDataCollector(final boolean parallel, final ObjectProfile<MapFactory> mapFactoryProfile, final ObjectFactory<MetricDatum> metricDatumFactory, final boolean clearGenerationsOnAdd, final boolean clearIterationsOnAdd) {
        this(parallel, mapFactoryProfile, metricDatumFactory, clearGenerationsOnAdd, clearIterationsOnAdd, createGenerationMetrics(mapFactoryProfile.getObject(), metricDatumFactory), createFitnessMetrics(mapFactoryProfile.getObject(), metricDatumFactory), createMetrics(mapFactoryProfile.getObject(), metricDatumFactory));
    }

    public DualModeConfigurableMetricDataCollector(final boolean parallel, final ObjectProfile<MapFactory> mapFactoryProfile, final DualModeConfigurableMetricDataCollector metricDataCollector) {
        this(parallel, mapFactoryProfile, metricDataCollector.metricDatumFactory, metricDataCollector.clearGenerationsOnAdd, metricDataCollector.clearIterationsOnAdd, createGenerationMetrics(metricDataCollector.generationMetrics, mapFactoryProfile), createFitnessMetrics(metricDataCollector.fitnessMetrics, mapFactoryProfile), createMetrics(metricDataCollector.metrics, mapFactoryProfile));
    }

    private static TopologyMetricData createTopologyMetrics(final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new TopologyMetricData(metricDatumFactory.create(), metricDatumFactory.create());
    }

    private TopologyMetricData createTopologyMetrics() {
        return createTopologyMetrics(metricDatumFactory);
    }

    private static GenerationMetricData createGenerationMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new GenerationMetricData(mapFactory.create(), createTopologyMetrics(metricDatumFactory), new ArrayList<>());
    }

    private GenerationMetricData createGenerationMetrics() {
        return createGenerationMetrics(mapFactoryProfile.getObject(), metricDatumFactory);
    }

    private static FitnessMetricData createFitnessMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new FitnessMetricData(mapFactory.create(), metricDatumFactory.create());
    }

    private FitnessMetricData createFitnessMetrics() {
        return createFitnessMetrics(mapFactoryProfile.getObject(), metricDatumFactory);
    }

    private static MetricData createMetrics(final MapFactory mapFactory, final ObjectFactory<MetricDatum> metricDatumFactory) {
        return new MetricData(mapFactory.create(), metricDatumFactory.create());
    }

    private MetricData createMetrics() {
        return createMetrics(mapFactoryProfile.getObject(), metricDatumFactory);
    }

    @Override
    public void addTopology(final String speciesId, final int hiddenNodes, final int connections) {
        TopologyMetricData speciesTopologyMetrics = generationMetrics.getSpeciesTopology().computeIfAbsent(speciesId, sid -> createTopologyMetrics());

        speciesTopologyMetrics.getHiddenNodes().add((float) hiddenNodes);
        speciesTopologyMetrics.getConnections().add((float) connections);
    }

    @Override
    public void addFitness(final String speciesId, final float fitness) {
        fitnessMetrics.getSpecies().computeIfAbsent(speciesId, sid -> metricDatumFactory.create()).add(fitness);
    }

    @Override
    public void prepareNextFitnessCalculation() {
        for (MetricDatum metricDatum : fitnessMetrics.getSpecies().values()) {
            fitnessMetrics.getAll().merge(metricDatum);
        }

        generationMetrics.getFitness().add(fitnessMetrics);
        fitnessMetrics = createFitnessMetrics();
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
        for (TopologyMetricData speciesTopologyMetrics : generationMetrics.getSpeciesTopology().values()) {
            generationMetrics.getTopology().getHiddenNodes().merge(speciesTopologyMetrics.getHiddenNodes());
            generationMetrics.getTopology().getConnections().merge(speciesTopologyMetrics.getConnections());
        }

        metrics.getSpecies().add(generationMetrics.getSpeciesTopology().size());

        if (clearGenerationsOnAdd) {
            metrics.getGenerations().clear();
        }

        metrics.getGenerations().put(currentGeneration, generationMetrics);
        generationMetrics = createGenerationMetrics();
    }

    @Override
    public void prepareNextIteration(final Map<Integer, MetricData> allMetrics, final int iteration) {
        if (clearIterationsOnAdd) {
            allMetrics.clear();
        }

        allMetrics.put(iteration, metrics);
        metrics = createMetrics();
    }

    private static FitnessMetricData createFitnessMetrics(final FitnessMetricData fitnessMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, MetricDatum> species = mapFactoryProfile.getObject().create(fitnessMetrics.getSpecies());
        MetricDatum all = fitnessMetrics.getAll();

        return new FitnessMetricData(species, all);
    }

    private FitnessMetricData createFitnessMetrics(final FitnessMetricData fitnessMetrics) {
        return createFitnessMetrics(fitnessMetrics, mapFactoryProfile);
    }

    private static GenerationMetricData createGenerationMetrics(final GenerationMetricData generationMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, TopologyMetricData> speciesTopology = mapFactoryProfile.getObject().create(generationMetrics.getSpeciesTopology());
        TopologyMetricData topology = generationMetrics.getTopology();

        List<FitnessMetricData> fitness = generationMetrics.getFitness().stream()
                .map(fm -> createFitnessMetrics(fm, mapFactoryProfile))
                .collect(Collectors.toList());

        return new GenerationMetricData(speciesTopology, topology, fitness);
    }

    private GenerationMetricData createGenerationMetrics(final GenerationMetricData generationMetrics) {
        return createGenerationMetrics(generationMetrics, mapFactoryProfile);
    }

    private static MetricData createMetrics(final MetricData metrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<Integer, GenerationMetricData> generations = metrics.getGenerations().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createGenerationMetrics(e.getValue(), mapFactoryProfile)));

        Map<Integer, GenerationMetricData> generationsFixed = mapFactoryProfile.getObject().create(generations);
        MetricDatum species = metrics.getSpecies();

        return new MetricData(generationsFixed, species);
    }

    private MetricData createMetrics(final MetricData metrics) {
        return createMetrics(metrics, mapFactoryProfile);
    }

    @Override
    public void switchMode(final boolean concurrent) {
        parallel = concurrent;
        mapFactoryProfile.switchProfile(concurrent);
        generationMetrics = createGenerationMetrics(generationMetrics);
        fitnessMetrics = createFitnessMetrics(fitnessMetrics);
        metrics = createMetrics(metrics);
    }

    @Override
    public DualModeMap<Integer, MetricData> ensureMode(final DualModeMap<Integer, MetricData> allMetrics, final int numberOfThreads) {
        Map<Integer, MetricData> allMetricsFixed = allMetrics.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createMetrics(e.getValue())));

        return new DualModeMap<>(parallel, numberOfThreads, allMetricsFixed);
    }
}
