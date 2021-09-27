package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.settings.MetricCollectionType;
import com.dipasquale.ai.rl.neat.settings.MetricSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.ConfigurableMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollectorFactory;
import com.dipasquale.ai.rl.neat.speciation.metric.NoopMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric.DualModeMetricsCollector;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.data.structure.map.TandemMap;
import com.dipasquale.metric.EmptyValuesMetricDatumFactory;
import com.dipasquale.metric.LazyValuesMetricDatumFactory;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.factory.data.structure.map.MapFactoryProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMetricSupport implements Context.MetricSupport {
    private DualModeMetricsCollector metricsCollector;
    private int stagnationDropOffAge;
    private DualModeIntegerCounter iteration;
    private DualModeIntegerCounter generation;
    private DualModeMap<Integer, IterationMetrics> iterationsMetrics;

    private static MetricDatumFactory createMetricDatumFactory(final EnumSet<MetricCollectionType> type) {
        if (type.contains(MetricCollectionType.SKIP_NORMAL_DISTRIBUTION_METRICS)) {
            return new EmptyValuesMetricDatumFactory();
        }

        return new LazyValuesMetricDatumFactory();
    }

    private static DualModeMetricsCollector createMetricsCollector(final ParallelismSupport parallelismSupport, final EnumSet<MetricCollectionType> type) {
        ObjectProfile<MapFactory> mapFactoryProfile = MapFactoryProfile.createHash(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        if (!type.contains(MetricCollectionType.ENABLED)) {
            MetricsCollector metricsCollector = new NoopMetricsCollector();
            MetricsCollectorFactory metricsCollectorFactory = (MetricsCollectorFactory & Serializable) (mf, gm, fm, im) -> metricsCollector;

            return new DualModeMetricsCollector(parallelismSupport.isEnabled(), mapFactoryProfile, metricsCollectorFactory, metricsCollector);
        }

        MetricDatumFactory metricDatumFactory = createMetricDatumFactory(type);
        boolean clearFitnessOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_FITNESS_EVALUATION);
        boolean clearGenerationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_GENERATION);
        boolean clearIterationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_ITERATION);
        MetricsCollectorFactory metricsCollectorFactory = (MetricsCollectorFactory & Serializable) (mf, gm, fm, im) -> new ConfigurableMetricsCollector(mf, metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd, gm, fm, im);
        MetricsCollector metricsCollector = new ConfigurableMetricsCollector(mapFactoryProfile.getObject(), metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd);

        return new DualModeMetricsCollector(parallelismSupport.isEnabled(), mapFactoryProfile, metricsCollectorFactory, metricsCollector);
    }

    public static DefaultContextMetricSupport create(final ParallelismSupport parallelismSupport, final MetricSupport metricSupport, final SpeciationSupport speciationSupport) {
        DualModeMetricsCollector metricsCollector = createMetricsCollector(parallelismSupport, metricSupport.getType());
        int stagnationDropOffAge = speciationSupport.getStagnationDropOffAge().getSingletonValue(parallelismSupport);
        DualModeIntegerCounter generation = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeIntegerCounter iteration = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeMap<Integer, IterationMetrics> metrics = new DualModeMap<>(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        return new DefaultContextMetricSupport(metricsCollector, stagnationDropOffAge, generation, iteration, metrics);
    }

    @Override
    public void addCompositions(final Iterable<Species> allSpecies) {
        for (Species species : allSpecies) {
            metricsCollector.collectSpeciesComposition(species.getAge(), species.getStagnationPeriod(), species.isStagnant(stagnationDropOffAge));

            for (Organism organism : species.getOrganisms()) {
                metricsCollector.collectOrganismTopology(species.getId(), organism.getHiddenNodes(), organism.getConnections());
            }
        }

        metricsCollector.flushSpeciesComposition();
    }

    @Override
    public void addFitness(final Species species, final Organism organism) {
        metricsCollector.collectOrganismFitness(species.getId(), organism.getFitness());
    }

    @Override
    public void addSharedFitness(final Species species) {
        metricsCollector.collectSpeciesFitness(species.getSharedFitness());
    }

    @Override
    public void prepareNextFitnessCalculation() {
        metricsCollector.prepareNextFitnessCalculation();
    }

    @Override
    public void prepareNextGeneration() {
        metricsCollector.prepareNextGeneration(generation.current());
        generation.increment();
    }

    @Override
    public void prepareNextIteration() {
        generation.current(1);
        metricsCollector.prepareNextIteration(iterationsMetrics, iteration.increment() - 1);
    }

    private static <TKey, TValue> Map<TKey, TValue> createMap(final TKey key, final TValue value) {
        Map<TKey, TValue> map = new HashMap<>();

        map.put(key, value);

        return map;
    }

    private IterationMetrics mergeIterationsMetrics() {
        Map<Integer, GenerationMetrics> currentGenerations = createMap(generation.current(), metricsCollector.getGenerationMetrics());
        Map<Integer, GenerationMetrics> previousGenerations = metricsCollector.getIterationMetrics().getGenerations();
        Map<Integer, GenerationMetrics> generations = new TandemMap<>(currentGenerations, previousGenerations);

        return new IterationMetrics(generations, metricsCollector.getIterationMetrics().getSpeciesCount());
    }

    @Override
    public Map<Integer, IterationMetrics> getMetrics() {
        Map<Integer, IterationMetrics> currentIterations = createMap(iteration.current(), mergeIterationsMetrics());
        Map<Integer, IterationMetrics> previousIterations = iterationsMetrics;

        return new TandemMap<>(currentIterations, previousIterations);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.iterationsMetrics", iterationsMetrics);
    }

    private static DualModeMetricsCollector loadMetricsCollector(final DualModeMetricsCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return new DualModeMetricsCollector(false, MapFactoryProfile.createHash(false, 1), metricsCollector);
        }

        return new DualModeMetricsCollector(true, MapFactoryProfile.createHash(true, eventLoop.getConcurrencyLevel()), metricsCollector);
    }

    private static DualModeMap<Integer, IterationMetrics> loadAllMetrics(final DualModeMap<Integer, IterationMetrics> allMetrics, final DualModeMetricsCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return metricsCollector.ensureMode(allMetrics, 1);
        }

        return metricsCollector.ensureMode(allMetrics, eventLoop.getConcurrencyLevel());
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        metricsCollector = loadMetricsCollector(stateGroup.get("metrics.metricsCollector"), eventLoop);
        stagnationDropOffAge = stateGroup.get("metrics.stagnationDropOffAge");
        iteration = DualModeObject.switchMode(stateGroup.get("metrics.iteration"), eventLoop != null);
        generation = DualModeObject.switchMode(stateGroup.get("metrics.generation"), eventLoop != null);
        iterationsMetrics = loadAllMetrics(stateGroup.get("metrics.iterationsMetrics"), metricsCollector, eventLoop);
    }
}
