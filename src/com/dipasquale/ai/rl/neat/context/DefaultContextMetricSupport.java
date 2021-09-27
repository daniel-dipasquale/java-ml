package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.settings.MetricCollectionType;
import com.dipasquale.ai.rl.neat.settings.MetricSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.ConfigurableMetricDataCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricDataCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricDataCollectorFactory;
import com.dipasquale.ai.rl.neat.speciation.metric.NoopMetricDataCollector;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric.DualModeMetricDataCollector;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.data.structure.map.TandemMap;
import com.dipasquale.metric.EmptyValuesMetricDatum;
import com.dipasquale.metric.LazyValuesMetricDatum;
import com.dipasquale.metric.MetricDatum;
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
    private DualModeMetricDataCollector metricsCollector;
    private int stagnationDropOffAge;
    private DualModeIntegerCounter iteration;
    private DualModeIntegerCounter generation;
    private DualModeMap<Integer, IterationMetricData> iterationsMetrics;

    private static ObjectFactory<MetricDatum> createMetricDatumFactory(final EnumSet<MetricCollectionType> type) {
        if (type.contains(MetricCollectionType.SKIP_NORMAL_DISTRIBUTION_METRICS)) {
            return (ObjectFactory<MetricDatum> & Serializable) EmptyValuesMetricDatum::new;
        }

        return (ObjectFactory<MetricDatum> & Serializable) LazyValuesMetricDatum::new;
    }

    private static DualModeMetricDataCollector createMetricsCollector(final ParallelismSupport parallelismSupport, final EnumSet<MetricCollectionType> type) {
        ObjectProfile<MapFactory> mapFactoryProfile = MapFactoryProfile.createHash(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        if (!type.contains(MetricCollectionType.ENABLED)) {
            MetricDataCollector metricsCollector = new NoopMetricDataCollector();
            MetricDataCollectorFactory metricsCollectorFactory = (MetricDataCollectorFactory & Serializable) (mf, gm, fm, im) -> metricsCollector;

            return new DualModeMetricDataCollector(parallelismSupport.isEnabled(), mapFactoryProfile, metricsCollectorFactory, metricsCollector);
        }

        ObjectFactory<MetricDatum> metricDatumFactory = createMetricDatumFactory(type);
        boolean clearFitnessOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_FITNESS_EVALUATION);
        boolean clearGenerationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_GENERATION);
        boolean clearIterationsOnAdd = type.contains(MetricCollectionType.ONLY_KEEP_LAST_ITERATION);
        MetricDataCollectorFactory metricsCollectorFactory = (MetricDataCollectorFactory & Serializable) (mf, gm, fm, im) -> new ConfigurableMetricDataCollector(mf, metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd, gm, fm, im);
        MetricDataCollector metricsCollector = new ConfigurableMetricDataCollector(mapFactoryProfile.getObject(), metricDatumFactory, clearFitnessOnAdd, clearGenerationsOnAdd, clearIterationsOnAdd);

        return new DualModeMetricDataCollector(parallelismSupport.isEnabled(), mapFactoryProfile, metricsCollectorFactory, metricsCollector);
    }

    public static DefaultContextMetricSupport create(final ParallelismSupport parallelismSupport, final MetricSupport metricSupport, final SpeciationSupport speciationSupport) {
        DualModeMetricDataCollector metricsCollector = createMetricsCollector(parallelismSupport, metricSupport.getType());
        int stagnationDropOffAge = speciationSupport.getStagnationDropOffAge().getSingleton(parallelismSupport);
        DualModeIntegerCounter generation = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeIntegerCounter iteration = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeMap<Integer, IterationMetricData> metrics = new DualModeMap<>(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        return new DefaultContextMetricSupport(metricsCollector, stagnationDropOffAge, generation, iteration, metrics);
    }

    @Override
    public void addTopology(final Species species, final Organism organism) {
        metricsCollector.addOrganismTopology(species.getId(), organism.getHiddenNodes(), organism.getConnections());
    }

    @Override
    public void addFitness(final Species species, final Organism organism) {
        metricsCollector.addOrganismFitness(species.getId(), organism.getFitness());
    }

    @Override
    public void addAttributes(final Species species) {
        metricsCollector.addSpeciesAttributes(species.getAge(), species.getStagnationPeriod(), species.isStagnant(stagnationDropOffAge));
    }

    @Override
    public void addSharedFitness(final Species species) {
        metricsCollector.addSpeciesFitness(species.getSharedFitness());
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

    private IterationMetricData mergeIterationsMetrics() {
        Map<Integer, GenerationMetricData> currentGenerations = createMap(generation.current(), metricsCollector.getCurrentGeneration());
        Map<Integer, GenerationMetricData> previousGenerations = metricsCollector.getCurrentIteration().getGenerations();
        Map<Integer, GenerationMetricData> generations = new TandemMap<>(currentGenerations, previousGenerations);

        return new IterationMetricData(generations, metricsCollector.getCurrentIteration().getSpecies());
    }

    @Override
    public Map<Integer, IterationMetricData> getMetrics() {
        Map<Integer, IterationMetricData> currentIterations = createMap(iteration.current(), mergeIterationsMetrics());
        Map<Integer, IterationMetricData> previousIterations = iterationsMetrics;

        return new TandemMap<>(currentIterations, previousIterations);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.iterationsMetrics", iterationsMetrics);
    }

    private static DualModeMetricDataCollector loadMetricsCollector(final DualModeMetricDataCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return new DualModeMetricDataCollector(false, MapFactoryProfile.createHash(false, 1), metricsCollector);
        }

        return new DualModeMetricDataCollector(true, MapFactoryProfile.createHash(true, eventLoop.getConcurrencyLevel()), metricsCollector);
    }

    private static DualModeMap<Integer, IterationMetricData> loadAllMetrics(final DualModeMap<Integer, IterationMetricData> allMetrics, final DualModeMetricDataCollector metricsCollector, final IterableEventLoop eventLoop) {
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
