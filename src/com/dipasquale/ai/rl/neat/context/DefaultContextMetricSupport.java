package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.settings.MetricCollectionType;
import com.dipasquale.ai.rl.neat.settings.MetricSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.ConfigurableMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.NoopMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric.DualModeMetricsContainer;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.data.structure.map.TandemMap;
import com.dipasquale.metric.EmptyValuesMetricDatumFactory;
import com.dipasquale.metric.LazyValuesMetricDatumFactory;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMetricSupport implements Context.MetricSupport {
    private DualModeMetricsContainer metricsContainer;
    private MetricsCollector metricsCollector;
    private int stagnationDropOffAge;
    private DualModeIntegerCounter iteration;
    private DualModeIntegerCounter generation;
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

    public static DefaultContextMetricSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final MetricSupport metricSupport, final SpeciationSupport speciationSupport) {
        MetricDatumFactory metricDatumFactory = createMetricDatumFactory(metricSupport.getType());
        DualModeMetricsContainer metricsContainer = new DualModeMetricsContainer(parallelismSupport.getMapFactory(), metricDatumFactory);
        MetricsCollector metricsCollector = createMetricsCollector(metricDatumFactory, metricSupport.getType());
        int stagnationDropOffAge = speciationSupport.getStagnationDropOffAge().getSingletonValue(parallelismSupport, randomSupports);
        DualModeIntegerCounter iteration = new DualModeIntegerCounter(parallelismSupport.getConcurrencyLevel(), 1);
        DualModeIntegerCounter generation = new DualModeIntegerCounter(parallelismSupport.getConcurrencyLevel(), 1);
        DualModeMap<Integer, IterationMetrics, DualModeMapFactory> metrics = new DualModeMap<>(parallelismSupport.getMapFactory());

        return new DefaultContextMetricSupport(metricsContainer, metricsCollector, stagnationDropOffAge, iteration, generation, metrics);
    }

    @Override
    public void collectCompositions(final Iterable<Species> allSpecies) {
        if (!metricsCollector.isEnabled()) {
            return;
        }

        for (Species species : allSpecies) {
            metricsCollector.collectSpeciesComposition(metricsContainer, species.getAge(), species.getStagnationPeriod(), species.isStagnant(stagnationDropOffAge));

            for (Organism organism : species.getOrganisms()) {
                metricsCollector.collectOrganismTopology(metricsContainer, species.getId(), organism.getHiddenNodes(), organism.getConnections());
            }
        }

        metricsCollector.flushSpeciesComposition(metricsContainer);
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
    public void prepareNextFitnessCalculation() {
        metricsCollector.prepareNextFitnessCalculation(metricsContainer);
    }

    @Override
    public void prepareNextGeneration() {
        metricsCollector.prepareNextGeneration(metricsContainer, generation.current());
        generation.increment();
    }

    @Override
    public void prepareNextIteration() {
        generation.current(1);
        metricsCollector.prepareNextIteration(metricsContainer, metrics, iteration.increment() - 1);
    }

    private static <TKey, TValue> Map<TKey, TValue> createMap(final TKey key, final TValue value) {
        Map<TKey, TValue> map = new HashMap<>();

        map.put(key, value);

        return map;
    }

    private IterationMetrics mergeIterationsMetrics() {
        Map<Integer, GenerationMetrics> currentGenerations = createMap(generation.current(), metricsContainer.getGenerationMetrics());
        Map<Integer, GenerationMetrics> previousGenerations = metricsContainer.getIterationMetrics().getGenerations();
        Map<Integer, GenerationMetrics> generations = new TandemMap<>(currentGenerations, previousGenerations);

        return new IterationMetrics(generations, metricsContainer.getIterationMetrics().getSpeciesCount());
    }

    public Map<Integer, IterationMetrics> getMetrics() {
        Map<Integer, IterationMetrics> currentIterations = createMap(iteration.current(), mergeIterationsMetrics());

        return new TandemMap<>(currentIterations, metrics);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.metricsContainer", metricsContainer);
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.metrics", metrics);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        metricsContainer = DualModeObject.activateMode(stateGroup.get("metrics.metricsContainer"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        metricsCollector = stateGroup.get("metrics.metricsCollector");
        stagnationDropOffAge = stateGroup.get("metrics.stagnationDropOffAge");
        iteration = DualModeObject.activateMode(stateGroup.get("metrics.iteration"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        generation = DualModeObject.activateMode(stateGroup.get("metrics.generation"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        metrics = DualModeObject.activateMode(stateGroup.get("metrics.metrics"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
