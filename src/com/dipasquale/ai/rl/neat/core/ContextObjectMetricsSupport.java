package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.ConfigurableMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.ai.rl.neat.speciation.metric.NoopMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric.DualModeMetricsContainer;
import com.dipasquale.data.structure.map.UnionMap;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.metric.EmptyValuesMetricDatumFactory;
import com.dipasquale.metric.LazyValuesMetricDatumFactory;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerValue;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectMetricsSupport implements Context.MetricsSupport {
    private ContextObjectMetricsParameters params;
    private MetricDatumFactory metricDatumFactory;
    private DualModeMetricsContainer metricsContainer;
    private MetricsCollector metricsCollector;
    private int stagnationDropOffAge;
    private DualModeIntegerValue iteration;
    private DualModeIntegerValue generation;
    private DualModeMap<Integer, IterationMetrics, DualModeMapFactory> metrics;

    private static MetricDatumFactory createMetricDatumFactory(final EnumSet<MetricCollectionType> type) {
        if (!type.contains(MetricCollectionType.ENABLED) || type.contains(MetricCollectionType.SKIP_NORMAL_DISTRIBUTION_METRICS)) {
            return new EmptyValuesMetricDatumFactory();
        }

        return new LazyValuesMetricDatumFactory();
    }

    private static MetricsCollector createMetricsCollector(final MetricDatumFactory metricDatumFactory, final EnumSet<MetricCollectionType> type) {
        if (!type.contains(MetricCollectionType.ENABLED)) {
            return new NoopMetricsCollector();
        }

        boolean clearFitnessOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_FITNESS_EVALUATION);
        boolean clearGenerationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_GENERATION);
        boolean clearIterationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_ITERATION);

        return new ConfigurableMetricsCollector(metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd);
    }

    static ContextObjectMetricsSupport create(final InitializationContext initializationContext, final MetricsSupport metricsSupport, final SpeciationSupport speciationSupport) {
        ContextObjectMetricsParameters params = ContextObjectMetricsParameters.builder()
                .enabled(metricsSupport.getType().contains(MetricCollectionType.ENABLED))
                .build();

        MetricDatumFactory metricDatumFactory = createMetricDatumFactory(metricsSupport.getType());
        DualModeMetricsContainer metricsContainer = new DualModeMetricsContainer(initializationContext.getMapFactory(), metricDatumFactory);
        MetricsCollector metricsCollector = createMetricsCollector(metricDatumFactory, metricsSupport.getType());
        int stagnationDropOffAge = speciationSupport.getStagnationDropOffAge().getSingletonValue(initializationContext);
        DualModeIntegerValue iteration = new DualModeIntegerValue(initializationContext.getConcurrencyLevel(), 1);
        DualModeIntegerValue generation = new DualModeIntegerValue(initializationContext.getConcurrencyLevel(), 1);
        DualModeMap<Integer, IterationMetrics, DualModeMapFactory> metrics = new DualModeMap<>(initializationContext.getMapFactory());

        return new ContextObjectMetricsSupport(params, metricDatumFactory, metricsContainer, metricsCollector, stagnationDropOffAge, iteration, generation, metrics);
    }

    @Override
    public Context.MetricsParameters params() {
        return params;
    }

    @Override
    public void collectInitialCompositions(final Iterable<Species> allSpecies) {
        if (!params.enabled()) {
            return;
        }

        for (Species species : allSpecies) {
            metricsCollector.collectSpeciesComposition(metricsContainer, species.getAge(), species.getStagnationPeriod(), species.isStagnant(stagnationDropOffAge));

            for (Organism organism : species.getOrganisms()) {
                metricsCollector.collectOrganismTopology(metricsContainer, species.getId(), organism.getHiddenNodes(), organism.getConnections());
            }
        }
    }

    @Override
    public void collectFitness(final Species species, final Organism organism) {
        metricsCollector.collectOrganismFitness(metricsContainer, species.getId(), organism.getFitness());
    }

    @Override
    public void collectFitness(final Species species) {
        metricsCollector.collectSpeciesFitness(metricsContainer, species.getSharedFitness());
    }

    @Override
    public void collectKilled(final Species species, final List<Organism> organismsKilled) {
        metricsCollector.collectOrganismsKilled(metricsContainer, species.getId(), organismsKilled.size());
    }

    @Override
    public void collectExtinction(final Species species, final boolean extinct) {
        metricsCollector.collectSpeciesExtinction(metricsContainer, extinct);
    }

    @Override
    public void prepareNextFitnessCalculation() {
        metricsCollector.prepareNextFitnessCalculation(metricsContainer);
    }

    @Override
    public void prepareNextGeneration() {
        metricsCollector.prepareNextGeneration(metricsContainer, generation.increment() - 1);
    }

    @Override
    public void prepareNextIteration() {
        metricsCollector.prepareNextIteration(metricsContainer, generation.current(), metrics, iteration.increment() - 1);
        generation.current(1);
    }

    private static <TKey, TValue> Map<TKey, TValue> createMap(final TKey key, final TValue value) {
        Map<TKey, TValue> map = new HashMap<>();

        map.put(key, value);

        return map;
    }

    private IterationMetrics mergeIterationsMetrics() {
        Map<Integer, GenerationMetrics> currentGenerations = createMap(generation.current(), metricsContainer.getGenerationMetrics());
        Map<Integer, GenerationMetrics> previousGenerations = metricsContainer.getIterationMetrics().getGenerations();
        Map<Integer, GenerationMetrics> generations = new UnionMap<>(currentGenerations, previousGenerations);

        return new IterationMetrics(generations);
    }

    @Override
    public MetricsViewer createMetricsViewer() {
        Map<Integer, IterationMetrics> currentIterations = createMap(iteration.current(), mergeIterationsMetrics());
        Map<Integer, IterationMetrics> metricsFixed = new UnionMap<>(currentIterations, metrics);

        return new MetricsViewer(metricsFixed, metricDatumFactory);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.params", params);
        stateGroup.put("metrics.metricDatumFactory", metricDatumFactory);
        stateGroup.put("metrics.metricsContainer", metricsContainer);
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.metrics", metrics);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        params = stateGroup.get("metrics.params");
        metricDatumFactory = stateGroup.get("metrics.metricDatumFactory");
        metricsContainer = DualModeObject.activateMode(stateGroup.get("metrics.metricsContainer"), concurrencyLevel);
        metricsCollector = stateGroup.get("metrics.metricsCollector");
        stagnationDropOffAge = stateGroup.get("metrics.stagnationDropOffAge");
        iteration = DualModeObject.activateMode(stateGroup.get("metrics.iteration"), concurrencyLevel);
        generation = DualModeObject.activateMode(stateGroup.get("metrics.generation"), concurrencyLevel);
        metrics = DualModeObject.activateMode(stateGroup.get("metrics.metrics"), concurrencyLevel);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
