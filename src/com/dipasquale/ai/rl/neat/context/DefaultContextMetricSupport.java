package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.settings.MetricCollectionType;
import com.dipasquale.ai.rl.neat.settings.MetricSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricData;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric.DualModeConfigurableMetricDataCollector;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric.DualModeMetricDataCollector;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric.DualModeNoopMetricDataCollector;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.metric.LazyMetricDatum;
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
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextMetricSupport implements Context.MetricSupport {
    private DualModeMetricDataCollector metricsCollector;
    private DualModeIntegerCounter iteration;
    private DualModeIntegerCounter generation;
    private DualModeMap<Integer, MetricData> metrics;

    private static DualModeMetricDataCollector createMetricsCollector(final ParallelismSupport parallelismSupport, final EnumSet<MetricCollectionType> type) {
        if (!type.contains(MetricCollectionType.FULL)) {
            return new DualModeNoopMetricDataCollector(parallelismSupport.isEnabled());
        }

        ObjectProfile<MapFactory> mapFactoryProfile = MapFactoryProfile.createHash(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());
        ObjectFactory<MetricDatum> metricDatumFactory = (ObjectFactory<MetricDatum> & Serializable) LazyMetricDatum::new;

        return DualModeConfigurableMetricDataCollector.builder()
                .parallel(parallelismSupport.isEnabled())
                .mapFactoryProfile(mapFactoryProfile)
                .metricDatumFactory(metricDatumFactory)
                .clearGenerationsOnAdd(type.contains(MetricCollectionType.ONLY_KEEP_LAST_GENERATION))
                .clearIterationsOnAdd(type.contains(MetricCollectionType.ONLY_KEEP_LAST_ITERATION))
                .build();
    }

    public static DefaultContextMetricSupport create(final ParallelismSupport parallelismSupport, final MetricSupport metricSupport) {
        DualModeMetricDataCollector metricsCollector = createMetricsCollector(parallelismSupport, metricSupport.getType());
        DualModeIntegerCounter generation = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeIntegerCounter iteration = new DualModeIntegerCounter(parallelismSupport.isEnabled(), 1);
        DualModeMap<Integer, MetricData> metrics = new DualModeMap<>(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        return new DefaultContextMetricSupport(metricsCollector, generation, iteration, metrics);
    }

    @Override
    public Map<Integer, MetricData> getMetrics() {
        return metrics;
    }

    @Override
    public void addTopology(final String speciesId, final int hiddenNodes, final int connections) {
        metricsCollector.addTopology(speciesId, hiddenNodes, connections);
    }

    @Override
    public void addFitness(final String speciesId, final float fitness) {
        metricsCollector.addFitness(speciesId, fitness);
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
        metricsCollector.prepareNextIteration(metrics, iteration.increment() - 1);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.metrics", metrics);
    }

    private static DualModeMetricDataCollector loadConfigurableMetricsCollector(final DualModeConfigurableMetricDataCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return new DualModeConfigurableMetricDataCollector(false, MapFactoryProfile.createHash(false, 1), metricsCollector);
        }

        return new DualModeConfigurableMetricDataCollector(true, MapFactoryProfile.createHash(true, eventLoop.getConcurrencyLevel()), metricsCollector);
    }

    private static DualModeMetricDataCollector loadMetricsCollector(final DualModeMetricDataCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (metricsCollector instanceof DualModeConfigurableMetricDataCollector) {
            return loadConfigurableMetricsCollector((DualModeConfigurableMetricDataCollector) metricsCollector, eventLoop);
        }

        return new DualModeNoopMetricDataCollector(eventLoop != null);
    }

    private static DualModeMap<Integer, MetricData> loadMetrics(final DualModeMap<Integer, MetricData> metrics, final DualModeMetricDataCollector metricsCollector, final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return metricsCollector.ensureMode(metrics, 1);
        }

        return metricsCollector.ensureMode(metrics, eventLoop.getConcurrencyLevel());
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        metricsCollector = loadMetricsCollector(stateGroup.get("metrics.metricsCollector"), eventLoop);
        iteration = DualModeObject.switchMode(stateGroup.get("metrics.iteration"), eventLoop != null);
        generation = DualModeObject.switchMode(stateGroup.get("metrics.generation"), eventLoop != null);
        metrics = loadMetrics(stateGroup.get("metrics.metrics"), metricsCollector, eventLoop);
    }
}
