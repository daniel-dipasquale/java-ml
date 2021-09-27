package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.FitnessMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollectorFactory;
import com.dipasquale.ai.rl.neat.speciation.metric.TopologyMetrics;
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
public final class DualModeMetricsCollector implements DualModeObject, MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -30170807481135566L;
    private boolean parallel;
    private final ObjectProfile<MapFactory> mapFactoryProfile;
    private final MetricsCollectorFactory metricsCollectorFactory;
    private MetricsCollector metricsCollector;

    public DualModeMetricsCollector(final boolean parallel, final ObjectProfile<MapFactory> mapFactoryProfile, final DualModeMetricsCollector other) {
        this(parallel, mapFactoryProfile, other.metricsCollectorFactory, other.metricsCollector);
    }

    @Override
    public FitnessMetrics getFitnessMetrics() {
        return metricsCollector.getFitnessMetrics();
    }

    @Override
    public GenerationMetrics getGenerationMetrics() {
        return metricsCollector.getGenerationMetrics();
    }

    @Override
    public IterationMetrics getIterationMetrics() {
        return metricsCollector.getIterationMetrics();
    }

    @Override
    public void collectSpeciesComposition(final int age, final int stagnationPeriod, final boolean isStagnant) {
        metricsCollector.collectSpeciesComposition(age, stagnationPeriod, isStagnant);
    }

    @Override
    public void collectOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
        metricsCollector.collectOrganismTopology(speciesId, hiddenNodes, connections);
    }

    @Override
    public void flushSpeciesComposition() {
        metricsCollector.flushSpeciesComposition();
    }

    @Override
    public void collectOrganismFitness(final String speciesId, final float fitness) {
        metricsCollector.collectOrganismFitness(speciesId, fitness);
    }

    @Override
    public void collectSpeciesFitness(final float fitness) {
        metricsCollector.collectSpeciesFitness(fitness);
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
    public void prepareNextIteration(final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
        metricsCollector.prepareNextIteration(iterationsMetrics, currentIteration);
    }

    private static FitnessMetrics createFitnessMetrics(final FitnessMetrics fitnessMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, MetricDatum> organisms = mapFactoryProfile.getObject().create(fitnessMetrics.getOrganisms());
        MetricDatum all = fitnessMetrics.getAll();
        MetricDatum shared = fitnessMetrics.getShared();

        return new FitnessMetrics(organisms, all, shared);
    }

    private FitnessMetrics createFitnessMetrics(final FitnessMetrics fitnessMetrics) {
        return createFitnessMetrics(fitnessMetrics, mapFactoryProfile);
    }

    private static GenerationMetrics createGenerationMetrics(final GenerationMetrics generationMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<String, TopologyMetrics> organismsTopology = mapFactoryProfile.getObject().create(generationMetrics.getOrganismsTopology());
        TopologyMetrics speciesTopology = generationMetrics.getSpeciesTopology();

        List<FitnessMetrics> organismsFitness = generationMetrics.getOrganismsFitness().stream()
                .map(fm -> createFitnessMetrics(fm, mapFactoryProfile))
                .collect(Collectors.toList());

        MetricDatum speciesAllFitness = generationMetrics.getSpeciesAllFitness();
        MetricDatum speciesSharedFitness = generationMetrics.getSpeciesSharedFitness();
        MetricDatum speciesAge = generationMetrics.getSpeciesAge();
        MetricDatum speciesStagnationPeriod = generationMetrics.getSpeciesStagnationPeriod();
        MetricDatum speciesStagnating = generationMetrics.getSpeciesStagnant();

        return new GenerationMetrics(organismsTopology, speciesTopology, organismsFitness, speciesAllFitness, speciesSharedFitness, speciesAge, speciesStagnationPeriod, speciesStagnating);
    }

    private GenerationMetrics createGenerationMetrics(final GenerationMetrics generationMetrics) {
        return createGenerationMetrics(generationMetrics, mapFactoryProfile);
    }

    private static IterationMetrics createIterationMetrics(final IterationMetrics iterationMetrics, final ObjectProfile<MapFactory> mapFactoryProfile) {
        Map<Integer, GenerationMetrics> generations = iterationMetrics.getGenerations().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createGenerationMetrics(e.getValue(), mapFactoryProfile)));

        Map<Integer, GenerationMetrics> generationsFixed = mapFactoryProfile.getObject().create(generations);
        MetricDatum species = iterationMetrics.getSpeciesCount();

        return new IterationMetrics(generationsFixed, species);
    }

    private IterationMetrics createIterationMetrics(final IterationMetrics iterationMetrics) {
        return createIterationMetrics(iterationMetrics, mapFactoryProfile);
    }

    private MetricsCollector switchMetricsCollectorMode() {
        GenerationMetrics currentGenerationMetrics = createGenerationMetrics(metricsCollector.getGenerationMetrics());
        FitnessMetrics currentFitnessMetrics = createFitnessMetrics(metricsCollector.getFitnessMetrics());
        IterationMetrics currentIterationMetrics = createIterationMetrics(metricsCollector.getIterationMetrics());

        return metricsCollectorFactory.create(mapFactoryProfile.getObject(), currentGenerationMetrics, currentFitnessMetrics, currentIterationMetrics);
    }

    @Override
    public void switchMode(final boolean concurrent) {
        parallel = concurrent;
        mapFactoryProfile.switchProfile(concurrent);
        metricsCollector = switchMetricsCollectorMode();
    }

    public DualModeMap<Integer, IterationMetrics> ensureMode(final DualModeMap<Integer, IterationMetrics> iterationsMetrics, final int numberOfThreads) {
        Map<Integer, IterationMetrics> iterationsMetricsFixed = iterationsMetrics.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createIterationMetrics(e.getValue())));

        return new DualModeMap<>(parallel, numberOfThreads, iterationsMetricsFixed);
    }
}
