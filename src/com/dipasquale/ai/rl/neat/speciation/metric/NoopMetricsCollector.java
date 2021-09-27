package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.EmptyValuesMetricDatumFactory;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class NoopMetricsCollector implements MetricsCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -3442321706503755754L;
    private static final MetricDatumFactory METRIC_DATUM_FACTORY = new EmptyValuesMetricDatumFactory();
    private final FitnessMetrics fitnessMetrics = new FitnessMetrics(new HashMap<>(), METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create());
    private final GenerationMetrics generationMetrics = createEmptyGeneration();
    private final IterationMetrics iterationMetrics = new IterationMetrics(new HashMap<>(), METRIC_DATUM_FACTORY.create());

    private static GenerationMetrics createEmptyGeneration() {
        TopologyMetrics emptyTopology = new TopologyMetrics(METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create());

        return new GenerationMetrics(new HashMap<>(), emptyTopology, new ArrayList<>(), METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create(), METRIC_DATUM_FACTORY.create());
    }

    @Override
    public void collectSpeciesComposition(final int age, final int stagnationPeriod, final boolean isStagnant) {
    }

    @Override
    public void collectOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
    }

    @Override
    public void flushSpeciesComposition() {

    }

    @Override
    public void collectOrganismFitness(final String speciesId, final float fitness) {
    }

    @Override
    public void collectSpeciesFitness(final float fitness) {
    }

    @Override
    public void prepareNextFitnessCalculation() {
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
    }

    @Override
    public void prepareNextIteration(final Map<Integer, IterationMetrics> iterationsMetrics, final int currentIteration) {
    }
}
