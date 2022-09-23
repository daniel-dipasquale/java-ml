package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.DefaultMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsContainer;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.ai.rl.neat.speciation.metric.NoopMetricsCollector;
import com.dipasquale.ai.rl.neat.speciation.metric.StandardMetricsContainer;
import com.dipasquale.ai.rl.neat.speciation.metric.concurrent.IsolatedMetricContainer;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.StandardIntegerValue;
import com.dipasquale.data.structure.map.UnionMap;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.metric.LightMetricDatumFactory;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.StandardMetricDatumFactory;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectMetricsSupport implements Context.MetricsSupport {
    private final ContextObjectMetricsParameters params;
    private final MetricDatumFactory metricDatumFactory;
    private final MetricsContainer metricsContainer;
    private final MetricsCollector metricsCollector;
    private final int stagnationDropOffAge;
    private final IntegerValue iteration;
    private final IntegerValue generation;
    private final Map<Integer, IterationMetrics> allIterationMetrics;

    private static MetricDatumFactory createMetricDatumFactory(final EnumSet<MetricCollectionType> types) {
        if (!types.contains(MetricCollectionType.ENABLED) || types.contains(MetricCollectionType.SKIP_NORMAL_DISTRIBUTION_METRICS)) {
            return LightMetricDatumFactory.getInstance();
        }

        return StandardMetricDatumFactory.getInstance();
    }

    private static MetricsContainer createMetricsContainer(final Set<Long> threadIds, final MetricDatumFactory metricDatumFactory, final IterationMetrics iterationMetrics) {
        IterationMetrics fixedIterationMetrics = Objects.requireNonNullElseGet(iterationMetrics, IterationMetrics::new);

        if (threadIds.isEmpty()) {
            return new StandardMetricsContainer(metricDatumFactory, fixedIterationMetrics);
        }

        return new IsolatedMetricContainer(threadIds, metricDatumFactory, fixedIterationMetrics);
    }

    private static MetricsCollector createMetricsCollector(final EnumSet<MetricCollectionType> types) {
        if (!types.contains(MetricCollectionType.ENABLED)) {
            return NoopMetricsCollector.getInstance();
        }

        boolean clearFitnessOnNext = types.contains(MetricCollectionType.ONLY_KEEP_LAST_FITNESS_EVALUATION);
        boolean clearGenerationsOnNext = types.contains(MetricCollectionType.ONLY_KEEP_LAST_GENERATION);
        boolean clearIterationsOnNext = types.contains(MetricCollectionType.ONLY_KEEP_LAST_ITERATION);

        return new DefaultMetricsCollector(clearFitnessOnNext, clearGenerationsOnNext, clearIterationsOnNext);
    }

    static ContextObjectMetricsSupport create(final InitializationContext initializationContext, final MetricsSettings metricsSettings, final SpeciationSettings speciationSettings) {
        ContextObjectMetricsParameters params = ContextObjectMetricsParameters.builder()
                .enabled(metricsSettings.getTypes().contains(MetricCollectionType.ENABLED))
                .build();

        MetricDatumFactory metricDatumFactory = createMetricDatumFactory(metricsSettings.getTypes());
        MetricsContainer metricsContainer = createMetricsContainer(initializationContext.getThreadIds(), metricDatumFactory, null);
        MetricsCollector metricsCollector = createMetricsCollector(metricsSettings.getTypes());
        int stagnationDropOffAge = initializationContext.provideSingleton(speciationSettings.getStagnationDropOffAge());
        IntegerValue iteration = new StandardIntegerValue(1);
        IntegerValue generation = new StandardIntegerValue(1);
        Map<Integer, IterationMetrics> allIterationMetrics = new HashMap<>();

        return new ContextObjectMetricsSupport(params, metricDatumFactory, metricsContainer, metricsCollector, stagnationDropOffAge, iteration, generation, allIterationMetrics);
    }

    @Override
    public Context.MetricsParameters params() {
        return params;
    }

    @Override
    public void collectAllSpeciesCompositions(final Iterable<Species> allSpecies) {
        metricsCollector.collectAllSpeciesCompositions(metricsContainer, allSpecies, stagnationDropOffAge);
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
    public void prepareNextFitnessEvaluation() {
        metricsCollector.prepareNextFitnessEvaluation(metricsContainer);
    }

    @Override
    public void prepareNextGeneration() {
        metricsCollector.prepareNextGeneration(metricsContainer, generation.increment() - 1);
    }

    @Override
    public void prepareNextIteration() {
        metricsCollector.prepareNextIteration(metricsContainer, generation.current(), allIterationMetrics, iteration.increment() - 1);
        generation.current(1);
    }

    @Override
    public MetricsViewer createMetricsViewer() {
        Map<Integer, IterationMetrics> currentIterationMetrics = Map.of(iteration.current(), metricsContainer.createInterimIterationCopy(generation.current()));
        Map<Integer, IterationMetrics> fixedIterationMetrics = new UnionMap<>(currentIterationMetrics, allIterationMetrics);

        return new MetricsViewer(fixedIterationMetrics, metricDatumFactory);
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("metrics.params", params);
        stateGroup.put("metrics.metricDatumFactory", metricDatumFactory);
        stateGroup.put("metrics.iterationMetrics", metricsContainer.createIterationCopy());
        stateGroup.put("metrics.metricsCollector", metricsCollector);
        stateGroup.put("metrics.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("metrics.iteration", iteration);
        stateGroup.put("metrics.generation", generation);
        stateGroup.put("metrics.allIterationMetrics", allIterationMetrics);
    }

    static ContextObjectMetricsSupport create(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop) {
        ContextObjectMetricsParameters params = stateGroup.get("metrics.params");
        MetricDatumFactory metricDatumFactory = stateGroup.get("metrics.metricDatumFactory");
        IterationMetrics iterationMetrics = stateGroup.get("metrics.iterationMetrics");
        MetricsContainer metricsContainer = createMetricsContainer(ParallelismSettings.getThreadIds(eventLoop), metricDatumFactory, iterationMetrics);
        MetricsCollector metricsCollector = stateGroup.get("metrics.metricsCollector");
        int stagnationDropOffAge = stateGroup.get("metrics.stagnationDropOffAge");
        IntegerValue iteration = stateGroup.get("metrics.iteration");
        IntegerValue generation = stateGroup.get("metrics.generation");
        Map<Integer, IterationMetrics> allIterationMetrics = stateGroup.get("metrics.allIterationMetrics");

        return new ContextObjectMetricsSupport(params, metricDatumFactory, metricsContainer, metricsCollector, stagnationDropOffAge, iteration, generation, allIterationMetrics);
    }
}
