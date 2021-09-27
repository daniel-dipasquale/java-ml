package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.FitnessMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricDataCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricDataCollectorFactory;
import com.dipasquale.ai.rl.neat.speciation.metric.TopologyMetricData;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class DualModeMetricDataCollector implements DualModeObject, MetricDataCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -30170807481135566L;
    private boolean parallel;
    private final ObjectProfile<MapFactory> mapFactoryProfile;
    private final MetricDataCollectorFactory metricsCollectorFactory;
    private MetricDataCollector metricsCollector;

    public DualModeMetricDataCollector(final boolean parallel, final ObjectProfile<MapFactory> mapFactoryProfile, final DualModeMetricDataCollector other) {
        this(parallel, mapFactoryProfile, other.metricsCollectorFactory, other.metricsCollector);
    }

    @Override
    public FitnessMetricData getCurrentFitness() {
        return metricsCollector.getCurrentFitness();
    }

    @Override
    public GenerationMetricData getCurrentGeneration() {
        return metricsCollector.getCurrentGeneration();
    }

    @Override
    public IterationMetricData getCurrentIteration() {
        return metricsCollector.getCurrentIteration();
    }

    @Override
    public void addOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
        metricsCollector.addOrganismTopology(speciesId, hiddenNodes, connections);
    }

    @Override
    public void addOrganismFitness(final String speciesId, final float fitness) {
        metricsCollector.addOrganismFitness(speciesId, fitness);
    }

    @Override
    public void addSpeciesAttributes(final int age, final int stagnationPeriod, final boolean isStagnant) {
        metricsCollector.addSpeciesAttributes(age, stagnationPeriod, isStagnant);
    }

    @Override
    public void addSpeciesFitness(final float fitness) {
        metricsCollector.addSpeciesFitness(fitness);
    }

    @Override
    public void prepareNextFitnessCalculation() {
        metricsCollector.prepareNextFitnessCalculation();
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
        metricsCollector.prepareNextGeneration(currentGeneration);
    }

    @Override
    public void prepareNextIteration(final Map<Integer, IterationMetricData> iterationsMetrics, final int currentIteration) {
        metricsCollector.prepareNextIteration(iterationsMetrics, currentIteration);
    }

    private static FitnessMetricData createFitnessMetrics(final FitnessMetricData fitnessMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, MetricDatum> organisms = mapFactoryProfile.getObject().create(fitnessMetrics.getOrganisms());
        MetricDatum species = fitnessMetrics.getSpecies();

        return new FitnessMetricData(organisms, species);
    }

    private FitnessMetricData createFitnessMetrics(final FitnessMetricData fitnessMetrics) {
        return createFitnessMetrics(fitnessMetrics, mapFactoryProfile);
    }

    private static GenerationMetricData createGenerationMetrics(final GenerationMetricData generationMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, TopologyMetricData> organismsTopology = mapFactoryProfile.getObject().create(generationMetrics.getOrganismsTopology());
        TopologyMetricData speciesTopology = generationMetrics.getSpeciesTopology();

        List<FitnessMetricData> organismsFitness = generationMetrics.getOrganismsFitness().stream()
                .map(fm -> createFitnessMetrics(fm, mapFactoryProfile))
                .collect(Collectors.toList());

        MetricDatum speciesFitness = generationMetrics.getSpeciesFitness();
        MetricDatum speciesAge = generationMetrics.getSpeciesAge();
        MetricDatum speciesStagnationPeriod = generationMetrics.getSpeciesStagnationPeriod();
        MetricDatum speciesStagnating = generationMetrics.getSpeciesStagnating();

        return new GenerationMetricData(organismsTopology, speciesTopology, organismsFitness, speciesFitness, speciesAge, speciesStagnationPeriod, speciesStagnating);
    }

    private GenerationMetricData createGenerationMetrics(final GenerationMetricData generationMetrics) {
        return createGenerationMetrics(generationMetrics, mapFactoryProfile);
    }

    private static IterationMetricData createIterationMetrics(final IterationMetricData iterationMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<Integer, GenerationMetricData> generations = iterationMetrics.getGenerations().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createGenerationMetrics(e.getValue(), mapFactoryProfile)));

        Map<Integer, GenerationMetricData> generationsFixed = mapFactoryProfile.getObject().create(generations);
        MetricDatum species = iterationMetrics.getSpecies();

        return new IterationMetricData(generationsFixed, species);
    }

    private IterationMetricData createIterationMetrics(final IterationMetricData iterationMetrics) {
        return createIterationMetrics(iterationMetrics, mapFactoryProfile);
    }

    private MetricDataCollector switchMetricsCollectorMode() {
        GenerationMetricData currentGenerationMetrics = createGenerationMetrics(metricsCollector.getCurrentGeneration());
        FitnessMetricData currentFitnessMetrics = createFitnessMetrics(metricsCollector.getCurrentFitness());
        IterationMetricData currentIterationMetrics = createIterationMetrics(metricsCollector.getCurrentIteration());

        return metricsCollectorFactory.create(mapFactoryProfile.getObject(), currentGenerationMetrics, currentFitnessMetrics, currentIterationMetrics);
    }

    @Override
    public void switchMode(final boolean concurrent) {
        parallel = concurrent;
        mapFactoryProfile.switchProfile(concurrent);
        metricsCollector = switchMetricsCollectorMode();
    }

    public DualModeMap<Integer, IterationMetricData> ensureMode(final DualModeMap<Integer, IterationMetricData> iterationsMetrics, final int numberOfThreads) {
        Map<Integer, IterationMetricData> iterationsMetricsFixed = iterationsMetrics.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createIterationMetrics(e.getValue())));

        return new DualModeMap<>(parallel, numberOfThreads, iterationsMetricsFixed);
    }
}
